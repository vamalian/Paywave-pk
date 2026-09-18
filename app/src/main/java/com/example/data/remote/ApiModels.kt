package com.example.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class RegisterRequest(
    val mobileNumber: String,
    val fullName: String,
    val dateOfBirth: String,
    val cnicNumber: String,
    val deviceId: String
)

data class RegisterResponse(
    val userId: String,
    val otpSessionId: String,
    val requiresOtp: Boolean,
    val maskedMobile: String
)

data class VerifyOtpRequest(
    val mobileNumber: String,
    val otpSessionId: String,
    val otpCode: String
)

data class VerifyOtpResponse(
    val isValid: Boolean,
    val tempToken: String,
    val isPinSetupRequired: Boolean
)

data class SetupPinRequest(
    val tempToken: String,
    val pinHash: String,
    val salt: String
)

data class LoginRequest(
    val mobileNumber: String,
    val pinHash: String,
    val deviceFingerprint: String
)

data class AuthTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long,
    val userId: String,
    val maskedCnic: String,
    val kycStatus: String
)

data class KycSubmitRequest(
    val userId: String,
    val frontImageBase64: String?,
    val backImageBase64: String?,
    val selfieImageBase64: String?,
    val consentSigned: Boolean
)

data class KycStatusResponse(
    val status: String, // NOT_SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED, MANUAL_REVIEW
    val providerReference: String?,
    val verifiedAt: Long?,
    val remarks: String?
)

data class WalletBalanceDto(
    val walletId: String,
    val availableBalance: Double,
    val ledgerBalance: Double,
    val currency: String,
    val dailyLimitRemaining: Double,
    val monthlyLimitRemaining: Double
)

data class TransferRequest(
    val senderWalletId: String,
    val recipientIdentifier: String, // mobile number or wallet ID
    val amount: Double,
    val note: String,
    val idempotencyKey: String,
    val authorizationToken: String
)

data class BankTransferRequest(
    val senderWalletId: String,
    val bankCode: String,
    val accountOrIban: String,
    val recipientName: String,
    val amount: Double,
    val purpose: String,
    val idempotencyKey: String,
    val authorizationToken: String
)

data class RechargeRequest(
    val mobileNumber: String,
    val telcoNetwork: String, // JAZZ, ZONG, TELENOR, UFONE
    val rechargeType: String, // PREPAID, POSTPAID, BUNDLE
    val amount: Double,
    val idempotencyKey: String
)

data class BillPaymentRequest(
    val billerCode: String,
    val billerCategory: String, // ELECTRICITY, GAS, WATER, INTERNET, EDUCATION
    val consumerNumber: String,
    val amount: Double,
    val idempotencyKey: String
)

data class QrPaymentRequest(
    val qrPayload: String,
    val amount: Double,
    val idempotencyKey: String
)

data class TransactionResultDto(
    val transactionRef: String,
    val status: String,
    val amount: Double,
    val fee: Double,
    val availableBalanceAfter: Double,
    val providerReference: String,
    val timestamp: Long,
    val message: String
)
