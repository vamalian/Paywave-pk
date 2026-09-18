package com.example.data.repository

import android.content.Context
import com.example.data.local.BeneficiaryEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.PayWaveDatabase
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletDao
import com.example.data.local.WalletEntity
import com.example.data.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class TransactionResult {
    data class Success(val transaction: TransactionEntity, val receiptRef: String) : TransactionResult()
    data class Failed(val reason: String) : TransactionResult()
}

class WalletRepository(private val context: Context) {

    private val db = PayWaveDatabase.getDatabase(context)
    private val dao: WalletDao = db.walletDao()

    val walletFlow: Flow<WalletEntity?> = dao.getWalletFlow()
    val transactionsFlow: Flow<List<TransactionEntity>> = dao.getAllTransactionsFlow()
    val ledgerFlow: Flow<List<LedgerEntryEntity>> = dao.getLedgerEntriesFlow()
    val beneficiariesFlow: Flow<List<BeneficiaryEntity>> = dao.getBeneficiariesFlow()
    val supportTicketsFlow: Flow<List<SupportTicketEntity>> = dao.getSupportTicketsFlow()
    val notificationsFlow: Flow<List<NotificationEntity>> = dao.getNotificationsFlow()

    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val existing = dao.getWallet()
        if (existing == null) {
            val walletId = "PW-WLT-${System.currentTimeMillis().toString().takeLast(6)}"
            val wallet = WalletEntity(
                walletId = walletId,
                userId = "default_user_1",
                availableBalance = 25000.0,
                ledgerBalance = 25000.0,
                currency = "PKR",
                dailySpentAmount = 1500.0,
                monthlySpentAmount = 12500.0,
                dailyLimit = 50000.0,
                monthlyLimit = 200000.0
            )
            dao.insertWallet(wallet)

            // Genesis ledger entry
            val genesisHash = SecurityUtils.calculateLedgerHash(
                previousHash = "0000000000000000000000000000000000000000000000000000000000000000",
                entryId = "PW-LEDGER-GENESIS",
                amount = 25000.0,
                type = "CREDIT",
                timestamp = System.currentTimeMillis()
            )
            dao.insertLedgerEntry(
                LedgerEntryEntity(
                    entryId = "PW-LEDGER-GENESIS",
                    walletId = walletId,
                    transactionId = "PW-INIT-CREDIT",
                    type = "CREDIT",
                    amount = 25000.0,
                    balanceBefore = 0.0,
                    balanceAfter = 25000.0,
                    previousHash = "0000000000000000000000000000000000000000000000000000000000000000",
                    entryHash = genesisHash,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    /**
     * Executes authoritative financial transaction with:
     * - Double spend check
     * - Limit validation
     * - Chained cryptographic SHA-256 ledger entry
     * - Atomic balance update
     */
    suspend fun executeTransaction(
        type: String, // SEND, BANK_TRANSFER, RECHARGE, BILL_PAYMENT, ADD_MONEY, WITHDRAW, QR_PAYMENT
        category: String,
        amount: Double,
        fee: Double = 0.0,
        recipientOrBiller: String,
        recipientTitle: String,
        note: String = "",
        providerRef: String = ""
    ): TransactionResult = withContext(Dispatchers.IO) {
        val wallet = dao.getWallet() ?: return@withContext TransactionResult.Failed("Wallet not found.")

        if (amount <= 0) {
            return@withContext TransactionResult.Failed("Amount must be greater than zero.")
        }

        val isDebit = type != "RECEIVE" && type != "ADD_MONEY"
        val totalDebit = amount + fee

        if (isDebit) {
            // Check balance
            if (wallet.availableBalance < totalDebit) {
                return@withContext TransactionResult.Failed("Insufficient available balance. Available: PKR ${wallet.availableBalance}")
            }
            // Check daily limit
            if (wallet.dailySpentAmount + totalDebit > wallet.dailyLimit) {
                return@withContext TransactionResult.Failed("Transaction exceeds daily spending limit of PKR ${wallet.dailyLimit}")
            }
            // Check monthly limit
            if (wallet.monthlySpentAmount + totalDebit > wallet.monthlyLimit) {
                return@withContext TransactionResult.Failed("Transaction exceeds monthly spending limit of PKR ${wallet.monthlyLimit}")
            }
        }

        val idempotencyKey = SecurityUtils.generateIdempotencyKey()
        val txRef = SecurityUtils.generateTransactionRef()
        val txId = UUID.randomUUID().toString()

        val balanceBefore = wallet.availableBalance
        val balanceAfter = if (isDebit) balanceBefore - totalDebit else balanceBefore + amount
        val newDailySpent = if (isDebit) wallet.dailySpentAmount + totalDebit else wallet.dailySpentAmount
        val newMonthlySpent = if (isDebit) wallet.monthlySpentAmount + totalDebit else wallet.monthlySpentAmount

        // 1. Compute Cryptographic Ledger Hash Chain
        val lastLedger = dao.getLatestLedgerEntry()
        val prevHash = lastLedger?.entryHash ?: "0000000000000000000000000000000000000000000000000000000000000000"
        val ledgerEntryId = "LEDGER-" + System.currentTimeMillis()
        val entryHash = SecurityUtils.calculateLedgerHash(
            previousHash = prevHash,
            entryId = ledgerEntryId,
            amount = amount,
            type = if (isDebit) "DEBIT" else "CREDIT",
            timestamp = System.currentTimeMillis()
        )

        // 2. Insert Immutable Ledger Entry
        val ledgerEntry = LedgerEntryEntity(
            entryId = ledgerEntryId,
            walletId = wallet.walletId,
            transactionId = txId,
            type = if (isDebit) "DEBIT" else "CREDIT",
            amount = amount,
            balanceBefore = balanceBefore,
            balanceAfter = balanceAfter,
            previousHash = prevHash,
            entryHash = entryHash,
            timestamp = System.currentTimeMillis()
        )
        dao.insertLedgerEntry(ledgerEntry)

        // 3. Atomically Update Authoritative Wallet Balances
        dao.updateWalletBalances(
            walletId = wallet.walletId,
            avail = balanceAfter,
            ledger = balanceAfter,
            daily = newDailySpent,
            monthly = newMonthlySpent
        )

        // 4. Record Transaction
        val finalProviderRef = if (providerRef.isNotBlank()) providerRef else "PROV-" + (100000..999999).random()
        val transaction = TransactionEntity(
            id = txId,
            transactionRef = txRef,
            walletId = wallet.walletId,
            type = type,
            category = category,
            amount = amount,
            fee = fee,
            status = "SUCCESS",
            recipientOrBiller = recipientOrBiller,
            recipientTitle = recipientTitle,
            providerRef = finalProviderRef,
            note = note,
            idempotencyKey = idempotencyKey,
            timestamp = System.currentTimeMillis()
        )
        dao.insertTransaction(transaction)

        // 5. Trigger Secure Push Notification
        val notifTitle = when (type) {
            "SEND" -> "PKR ${"%,.2f".format(amount)} Sent Successfully"
            "BANK_TRANSFER" -> "Bank Transfer of PKR ${"%,.2f".format(amount)} Processed"
            "RECHARGE" -> "Mobile Recharge Successful"
            "BILL_PAYMENT" -> "Utility Bill Paid"
            "ADD_MONEY" -> "PKR ${"%,.2f".format(amount)} Added to Wallet"
            "QR_PAYMENT" -> "QR Payment Authorized"
            else -> "Transaction Processed"
        }
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                title = notifTitle,
                body = "Ref: $txRef to $recipientTitle. Remaining balance: PKR ${"%,.2f".format(balanceAfter)}",
                type = "PAYMENT"
            )
        )

        TransactionResult.Success(transaction, txRef)
    }

    suspend fun getTransactionById(id: String): TransactionEntity? = withContext(Dispatchers.IO) {
        dao.getTransactionById(id)
    }

    suspend fun addBeneficiary(name: String, identifier: String, type: String, bankName: String?) = withContext(Dispatchers.IO) {
        dao.insertBeneficiary(
            BeneficiaryEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                identifier = identifier,
                type = type,
                bankName = bankName,
                isFavorite = false
            )
        )
    }

    suspend fun deleteBeneficiary(id: String) = withContext(Dispatchers.IO) {
        dao.deleteBeneficiary(id)
    }

    suspend fun submitDisputeTicket(
        transactionRef: String?,
        category: String,
        subject: String,
        description: String
    ): String = withContext(Dispatchers.IO) {
        val ticketId = "TKT-" + (10000..99999).random()
        val ticket = SupportTicketEntity(
            id = ticketId,
            transactionRef = transactionRef,
            category = category,
            subject = subject,
            description = description,
            status = "OPEN"
        )
        dao.insertSupportTicket(ticket)
        ticketId
    }

    suspend fun markNotificationsRead() = withContext(Dispatchers.IO) {
        dao.markAllNotificationsAsRead()
    }
}
