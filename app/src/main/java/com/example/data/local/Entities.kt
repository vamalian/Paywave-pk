package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val mobileNumber: String,
    val fullName: String,
    val dateOfBirth: String,
    val maskedCnic: String,
    val kycStatus: String, // NOT_SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED, MANUAL_REVIEW
    val isBiometricEnabled: Boolean,
    val pinHash: String,
    val pinSalt: String,
    val failedPinAttempts: Int = 0,
    val lockoutUntil: Long = 0L,
    val authToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey val walletId: String,
    val userId: String,
    val availableBalance: Double,
    val ledgerBalance: Double,
    val currency: String = "PKR",
    val dailySpentAmount: Double = 0.0,
    val monthlySpentAmount: Double = 0.0,
    val dailyLimit: Double = 50000.0, // SBP Tier-1 wallet limit
    val monthlyLimit: Double = 200000.0,
    val status: String = "ACTIVE" // ACTIVE, FROZEN, RESTRICTED
)

@Entity(tableName = "wallet_ledger")
data class LedgerEntryEntity(
    @PrimaryKey val entryId: String,
    val walletId: String,
    val transactionId: String,
    val type: String, // CREDIT, DEBIT
    val amount: Double,
    val balanceBefore: Double,
    val balanceAfter: Double,
    val previousHash: String,
    val entryHash: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val transactionRef: String,
    val walletId: String,
    val type: String, // SEND, RECEIVE, BANK_TRANSFER, RECHARGE, BILL_PAYMENT, ADD_MONEY, WITHDRAW, QR_PAYMENT
    val category: String,
    val amount: Double,
    val fee: Double = 0.0,
    val status: String, // SUCCESS, PENDING, FAILED, REVERSED, REFUNDED
    val recipientOrBiller: String,
    val recipientTitle: String,
    val providerRef: String,
    val note: String = "",
    val idempotencyKey: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val failureReason: String? = null
)

@Entity(tableName = "kyc_documents")
data class KycDocumentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val frontDocUri: String,
    val backDocUri: String,
    val selfieUri: String,
    val status: String, // UNDER_REVIEW, VERIFIED, REJECTED, MANUAL_REVIEW
    val verificationProvider: String = "NADRA_AUTHORIZED_IDENTITY_VERISYS",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val notes: String? = null
)

@Entity(tableName = "beneficiaries")
data class BeneficiaryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val identifier: String, // mobile number, bank account or IBAN
    val type: String, // WALLET, BANK
    val bankName: String? = null,
    val isFavorite: Boolean = false
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val transactionRef: String?,
    val category: String,
    val subject: String,
    val description: String,
    val status: String = "OPEN", // OPEN, IN_REVIEW, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "device_sessions")
data class DeviceSessionEntity(
    @PrimaryKey val sessionId: String,
    val deviceName: String,
    val deviceModel: String,
    val ipAddress: String,
    val lastActive: Long = System.currentTimeMillis(),
    val isCurrent: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
