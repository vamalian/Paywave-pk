package com.example.data.repository

import android.content.Context
import android.os.Build
import com.example.data.local.DeviceSessionEntity
import com.example.data.local.KycDocumentEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PayWaveDatabase
import com.example.data.local.UserEntity
import com.example.data.local.WalletDao
import com.example.data.local.WalletEntity
import com.example.data.security.KeystoreManager
import com.example.data.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class OtpSent(val mobile: String, val otpSessionId: String, val demoOtp: String) : AuthResult()
    data class Lockout(val remainingSeconds: Long) : AuthResult()
}

class AuthRepository(private val context: Context) {

    private val db = PayWaveDatabase.getDatabase(context)
    private val dao: WalletDao = db.walletDao()

    val currentUserFlow: Flow<UserEntity?> = dao.getCurrentUserFlow()
    val activeSessionsFlow: Flow<List<DeviceSessionEntity>> = dao.getDeviceSessionsFlow()

    // Temporary registration cache in memory during multi-step auth
    var pendingMobile: String = ""
    var pendingFullName: String = ""
    var pendingDob: String = ""
    var pendingCnic: String = ""
    var pendingOtpCode: String = ""
    var pendingOtpSessionId: String = ""
    var pendingOtpGeneratedAt: Long = 0L

    suspend fun registerInitiate(
        mobile: String,
        fullName: String,
        dob: String,
        cnic: String
    ): AuthResult = withContext(Dispatchers.IO) {
        if (!SecurityUtils.isValidPakistaniMobile(mobile)) {
            return@withContext AuthResult.Error("Invalid Pakistani mobile number. Must start with 03XX (e.g. 03001234567).")
        }
        if (fullName.trim().length < 3) {
            return@withContext AuthResult.Error("Full legal name as per CNIC is required.")
        }
        if (!SecurityUtils.isValidCnic(cnic)) {
            return@withContext AuthResult.Error("Invalid CNIC format. Must be 13 digits (XXXXX-XXXXXXX-X).")
        }

        val normalizedMobile = SecurityUtils.normalizePakistaniMobile(mobile)

        pendingMobile = normalizedMobile
        pendingFullName = fullName.trim()
        pendingDob = dob
        pendingCnic = cnic
        // In production, OTP is dispatched via authorized PTA-licensed SMS gateway (e.g. Telenor/Jazz SMS API)
        val generatedOtp = (100000..999999).random().toString()
        pendingOtpCode = generatedOtp
        pendingOtpSessionId = UUID.randomUUID().toString()
        pendingOtpGeneratedAt = System.currentTimeMillis()

        AuthResult.OtpSent(
            mobile = normalizedMobile,
            otpSessionId = pendingOtpSessionId,
            demoOtp = generatedOtp
        )
    }

    suspend fun verifyOtp(enteredOtp: String): AuthResult = withContext(Dispatchers.IO) {
        if (System.currentTimeMillis() - pendingOtpGeneratedAt > 120_000) {
            return@withContext AuthResult.Error("OTP has expired. Please request a new verification code.")
        }
        if (enteredOtp.trim() != pendingOtpCode) {
            return@withContext AuthResult.Error("Incorrect OTP code. Please enter the 6-digit code sent to your mobile.")
        }
        AuthResult.Success(
            UserEntity(
                id = "temp",
                mobileNumber = pendingMobile,
                fullName = pendingFullName,
                dateOfBirth = pendingDob,
                maskedCnic = SecurityUtils.maskCnic(pendingCnic),
                kycStatus = "NOT_SUBMITTED",
                isBiometricEnabled = false,
                pinHash = "",
                pinSalt = ""
            )
        )
    }

    suspend fun completeRegistrationWithPin(pin: String): AuthResult = withContext(Dispatchers.IO) {
        if (pin.length != 4 || !pin.all { it.isDigit() }) {
            return@withContext AuthResult.Error("PIN must be exactly 4 digits.")
        }

        val userId = UUID.randomUUID().toString()
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPin(pin, salt)
        val encryptedToken = KeystoreManager.encrypt("PW-AUTH-$userId-${System.currentTimeMillis()}")

        val user = UserEntity(
            id = userId,
            mobileNumber = pendingMobile,
            fullName = pendingFullName,
            dateOfBirth = pendingDob,
            maskedCnic = SecurityUtils.maskCnic(pendingCnic),
            kycStatus = "NOT_SUBMITTED",
            isBiometricEnabled = true,
            pinHash = hash,
            pinSalt = salt,
            authToken = encryptedToken,
            createdAt = System.currentTimeMillis()
        )

        dao.insertUser(user)

        // Initialize authoritative wallet ledger
        val walletId = "PW-WLT-" + (100000..999999).random()
        val wallet = WalletEntity(
            walletId = walletId,
            userId = userId,
            availableBalance = 25000.0, // Initial verified signup credit for instant testability
            ledgerBalance = 25000.0,
            currency = "PKR",
            dailyLimit = 50000.0,
            monthlyLimit = 200000.0
        )
        dao.insertWallet(wallet)

        // Record initial device session
        val session = DeviceSessionEntity(
            sessionId = UUID.randomUUID().toString(),
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            deviceModel = Build.DEVICE ?: "Android Device",
            ipAddress = "10.0.2.15 (Secure TLS)",
            lastActive = System.currentTimeMillis(),
            isCurrent = true
        )
        dao.insertDeviceSession(session)

        // Welcome notification
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Welcome to PayWave PK!",
                body = "Your biometric digital wallet account is now active. Complete CNIC verification to unlock higher limits.",
                type = "WELCOME"
            )
        )

        AuthResult.Success(user)
    }

    suspend fun loginWithPin(mobile: String, pin: String): AuthResult = withContext(Dispatchers.IO) {
        val normalizedMobile = SecurityUtils.normalizePakistaniMobile(mobile)
        val user = dao.getUserByMobile(normalizedMobile) ?: dao.getCurrentUser()

        if (user == null) {
            return@withContext AuthResult.Error("No account found with this mobile number. Please register first.")
        }

        // Check brute force lockout
        val now = System.currentTimeMillis()
        if (user.lockoutUntil > now) {
            val remSecs = (user.lockoutUntil - now) / 1000
            return@withContext AuthResult.Lockout(remSecs)
        }

        val computedHash = SecurityUtils.hashPin(pin, user.pinSalt)
        if (computedHash != user.pinHash) {
            val newAttempts = user.failedPinAttempts + 1
            if (newAttempts >= 3) {
                val lockoutTime = now + 60_000 // 1 minute lockout after 3 failed attempts
                dao.updatePinAttempts(user.id, 0, lockoutTime)
                return@withContext AuthResult.Lockout(60)
            } else {
                dao.updatePinAttempts(user.id, newAttempts, 0L)
                return@withContext AuthResult.Error("Incorrect PIN. Attempt $newAttempts of 3.")
            }
        }

        // Reset failed attempts on valid login
        dao.updatePinAttempts(user.id, 0, 0L)
        AuthResult.Success(user)
    }

    suspend fun toggleBiometric(userId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        dao.updateBiometricSetting(userId, enabled)
    }

    suspend fun changePin(userId: String, oldPin: String, newPin: String): Boolean = withContext(Dispatchers.IO) {
        val user = dao.getCurrentUser() ?: return@withContext false
        val oldHash = SecurityUtils.hashPin(oldPin, user.pinSalt)
        if (oldHash != user.pinHash) return@withContext false

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPin(newPin, newSalt)
        dao.updatePin(userId, newHash, newSalt)

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "Security Alert: PIN Changed",
                body = "Your 4-digit PayWave PK PIN was updated securely via Android Keystore.",
                type = "SECURITY"
            )
        )
        true
    }

    suspend fun submitKycDocuments(
        userId: String,
        frontUri: String,
        backUri: String,
        selfieUri: String
    ): Boolean = withContext(Dispatchers.IO) {
        val doc = KycDocumentEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            frontDocUri = frontUri,
            backDocUri = backUri,
            selfieUri = selfieUri,
            status = "UNDER_REVIEW",
            verificationProvider = "NADRA_AUTHORIZED_IDENTITY_VERISYS",
            submittedAt = System.currentTimeMillis()
        )
        dao.insertKycDocument(doc)
        dao.updateKycStatus(userId, "UNDER_REVIEW")

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "KYC Documents Submitted",
                body = "Your CNIC documents have been sent to authorized verification providers for review.",
                type = "KYC"
            )
        )
        true
    }

    suspend fun updateKycStatusByAdmin(userId: String, status: String, notes: String?) = withContext(Dispatchers.IO) {
        dao.updateKycStatus(userId, status)
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = "KYC Status Update: $status",
                body = notes ?: "Your identity verification status has been updated to $status.",
                type = "KYC"
            )
        )
    }

    suspend fun logoutAllOtherSessions() = withContext(Dispatchers.IO) {
        dao.deleteAllOtherSessions()
    }
}
