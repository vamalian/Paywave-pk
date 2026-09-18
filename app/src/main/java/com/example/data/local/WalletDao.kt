package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    // User Operations
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE mobileNumber = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET kycStatus = :status WHERE id = :userId")
    suspend fun updateKycStatus(userId: String, status: String)

    @Query("UPDATE users SET isBiometricEnabled = :enabled WHERE id = :userId")
    suspend fun updateBiometricSetting(userId: String, enabled: Boolean)

    @Query("UPDATE users SET pinHash = :newHash, pinSalt = :newSalt WHERE id = :userId")
    suspend fun updatePin(userId: String, newHash: String, newSalt: String)

    @Query("UPDATE users SET failedPinAttempts = :attempts, lockoutUntil = :lockout WHERE id = :userId")
    suspend fun updatePinAttempts(userId: String, attempts: Int, lockout: Long)

    // Wallet Operations
    @Query("SELECT * FROM wallets LIMIT 1")
    fun getWalletFlow(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets LIMIT 1")
    suspend fun getWallet(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Query("UPDATE wallets SET availableBalance = :avail, ledgerBalance = :ledger, dailySpentAmount = :daily, monthlySpentAmount = :monthly WHERE walletId = :walletId")
    suspend fun updateWalletBalances(walletId: String, avail: Double, ledger: Double, daily: Double, monthly: Double)

    // Immutable Ledger Operations
    @Query("SELECT * FROM wallet_ledger ORDER BY timestamp DESC")
    fun getLedgerEntriesFlow(): Flow<List<LedgerEntryEntity>>

    @Query("SELECT * FROM wallet_ledger ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLedgerEntry(): LedgerEntryEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity)

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    fun getTransactionsByCategoryFlow(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id OR transactionRef = :id LIMIT 1")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE idempotencyKey = :key LIMIT 1")
    suspend fun getTransactionByIdempotencyKey(key: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status, failureReason = :reason WHERE id = :id")
    suspend fun updateTransactionStatus(id: String, status: String, reason: String? = null)

    // KYC Documents
    @Query("SELECT * FROM kyc_documents WHERE userId = :userId ORDER BY submittedAt DESC LIMIT 1")
    fun getKycDocumentFlow(userId: String): Flow<KycDocumentEntity?>

    @Query("SELECT * FROM kyc_documents ORDER BY submittedAt DESC")
    fun getAllKycDocumentsFlow(): Flow<List<KycDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKycDocument(doc: KycDocumentEntity)

    @Query("UPDATE kyc_documents SET status = :status, notes = :notes, reviewedAt = :reviewedAt WHERE id = :docId")
    suspend fun updateKycDocumentStatus(docId: String, status: String, notes: String?, reviewedAt: Long)

    // Beneficiaries
    @Query("SELECT * FROM beneficiaries ORDER BY isFavorite DESC, name ASC")
    fun getBeneficiariesFlow(): Flow<List<BeneficiaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBeneficiary(beneficiary: BeneficiaryEntity)

    @Query("DELETE FROM beneficiaries WHERE id = :id")
    suspend fun deleteBeneficiary(id: String)

    // Support Tickets
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getSupportTicketsFlow(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)

    // Device Sessions
    @Query("SELECT * FROM device_sessions ORDER BY lastActive DESC")
    fun getDeviceSessionsFlow(): Flow<List<DeviceSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceSession(session: DeviceSessionEntity)

    @Query("DELETE FROM device_sessions WHERE isCurrent = 0")
    suspend fun deleteAllOtherSessions()

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotificationsFlow(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}
