package com.example.ui.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BeneficiaryEntity
import com.example.data.local.DeviceSessionEntity
import com.example.data.local.KycDocumentEntity
import com.example.data.local.LedgerEntryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserEntity
import com.example.data.local.WalletEntity
import com.example.data.remote.PaymentProviderRegistry
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthResult
import com.example.data.repository.TransactionResult
import com.example.data.repository.WalletRepository
import com.example.data.security.BiometricAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenRoute {
    SPLASH,
    LOGIN,
    REGISTER,
    OTP_VERIFY,
    SET_PIN,
    HOME,
    SEND_MONEY,
    BANK_TRANSFER,
    ADD_MONEY,
    WITHDRAW,
    RECEIVE_QR,
    SCAN_QR,
    MOBILE_RECHARGE,
    BILL_PAYMENT,
    TRANSACTIONS,
    RECEIPT,
    KYC_VERIFY,
    ADMIN_DASHBOARD,
    SUPPORT,
    SETTINGS
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val otpSentMobile: String = "",
    val otpDemoCode: String = "",
    val lockoutSeconds: Long = 0L,
    val currentRoute: ScreenRoute = ScreenRoute.SPLASH
)

class PayWaveViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository(application)
    private val walletRepo = WalletRepository(application)

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = authRepo.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val wallet: StateFlow<WalletEntity?> = walletRepo.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<TransactionEntity>> = walletRepo.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgerEntries: StateFlow<List<LedgerEntryEntity>> = walletRepo.ledgerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val beneficiaries: StateFlow<List<BeneficiaryEntity>> = walletRepo.beneficiariesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicketEntity>> = walletRepo.supportTicketsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = walletRepo.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSessions: StateFlow<List<DeviceSessionEntity>> = authRepo.activeSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states for transaction list
    val selectedCategoryFilter = MutableStateFlow("ALL")
    val selectedStatusFilter = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        transactions,
        selectedCategoryFilter,
        selectedStatusFilter,
        searchQuery
    ) { list, cat, stat, query ->
        list.filter { tx ->
            val matchesCategory = if (cat == "ALL") true else tx.category.equals(cat, ignoreCase = true) || tx.type.equals(cat, ignoreCase = true)
            val matchesStatus = if (stat == "ALL") true else tx.status.equals(stat, ignoreCase = true)
            val matchesQuery = query.isBlank() || tx.recipientTitle.contains(query, ignoreCase = true) ||
                    tx.recipientOrBiller.contains(query, ignoreCase = true) ||
                    tx.transactionRef.contains(query, ignoreCase = true)
            matchesCategory && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active receipt to display
    val currentReceiptTx = MutableStateFlow<TransactionEntity?>(null)

    // Security & Biometric sensitive action pending execution callback
    var pendingSensitiveAction: (() -> Unit)? = null
    val showPinVerificationDialog = MutableStateFlow(false)
    val sensitiveActionTitle = MutableStateFlow("Confirm Payment")

    init {
        viewModelScope.launch {
            walletRepo.ensureInitialized()
            // Check if user already exists
            authRepo.currentUserFlow.collect { user ->
                if (user != null && _authUiState.value.currentRoute == ScreenRoute.SPLASH) {
                    _authUiState.value = _authUiState.value.copy(
                        isLoggedIn = true,
                        currentRoute = ScreenRoute.HOME
                    )
                } else if (user == null && _authUiState.value.currentRoute == ScreenRoute.SPLASH) {
                    _authUiState.value = _authUiState.value.copy(
                        isLoggedIn = false,
                        currentRoute = ScreenRoute.LOGIN
                    )
                }
            }
        }
    }

    fun navigateTo(route: ScreenRoute) {
        _authUiState.value = _authUiState.value.copy(
            currentRoute = route,
            errorMessage = null,
            successMessage = null
        )
    }

    fun clearMessages() {
        _authUiState.value = _authUiState.value.copy(errorMessage = null, successMessage = null)
    }

    // --- Authentication Actions ---

    fun onRegisterInitiate(mobile: String, fullName: String, dob: String, cnic: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = authRepo.registerInitiate(mobile, fullName, dob, cnic)) {
                is AuthResult.OtpSent -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        otpSentMobile = res.mobile,
                        otpDemoCode = res.demoOtp,
                        currentRoute = ScreenRoute.OTP_VERIFY
                    )
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
                else -> Unit
            }
        }
    }

    fun onVerifyOtp(otp: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = authRepo.verifyOtp(otp)) {
                is AuthResult.Success -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        currentRoute = ScreenRoute.SET_PIN
                    )
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
                else -> Unit
            }
        }
    }

    fun onSetPin(pin: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = authRepo.completeRegistrationWithPin(pin)) {
                is AuthResult.Success -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentRoute = ScreenRoute.HOME,
                        successMessage = "Account registered successfully with biometric protection!"
                    )
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
                else -> Unit
            }
        }
    }

    fun onLoginWithPin(mobile: String, pin: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            when (val res = authRepo.loginWithPin(mobile, pin)) {
                is AuthResult.Success -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentRoute = ScreenRoute.HOME
                    )
                }
                is AuthResult.Error -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
                is AuthResult.Lockout -> {
                    _authUiState.value = _authUiState.value.copy(
                        isLoading = false,
                        lockoutSeconds = res.remainingSeconds,
                        errorMessage = "Account temporarily locked due to 3 failed PIN attempts. Try again in ${res.remainingSeconds}s."
                    )
                }
                else -> Unit
            }
        }
    }

    fun onBiometricLogin(activity: FragmentActivity) {
        val status = BiometricAuthManager.checkBiometricAvailability(activity)
        if (status == BiometricAuthManager.BiometricStatus.AVAILABLE) {
            BiometricAuthManager.promptBiometric(
                activity = activity,
                title = "PayWave PK Secure Login",
                subtitle = "Touch fingerprint sensor to unlock wallet",
                negativeButtonText = "Use PIN",
                onSuccess = {
                    _authUiState.value = _authUiState.value.copy(
                        isLoggedIn = true,
                        currentRoute = ScreenRoute.HOME
                    )
                },
                onError = { _, err ->
                    _authUiState.value = _authUiState.value.copy(
                        errorMessage = "Biometric error: $err"
                    )
                },
                onFailed = {
                    _authUiState.value = _authUiState.value.copy(
                        errorMessage = "Biometric not recognized. Please try again or use PIN."
                    )
                }
            )
        } else {
            _authUiState.value = _authUiState.value.copy(
                errorMessage = "Biometrics not available (${status.name}). Please use your secure 4-digit PIN."
            )
        }
    }

    fun onLogout() {
        _authUiState.value = _authUiState.value.copy(
            isLoggedIn = false,
            currentRoute = ScreenRoute.LOGIN
        )
    }

    // --- High-Security Sensitive Action Authorization Flow ---

    fun requestSensitiveActionAuthorization(
        activity: FragmentActivity?,
        title: String,
        action: () -> Unit
    ) {
        sensitiveActionTitle.value = title
        pendingSensitiveAction = action

        val user = currentUser.value
        val isBioEnabled = user?.isBiometricEnabled == true

        if (activity != null && isBioEnabled &&
            BiometricAuthManager.checkBiometricAvailability(activity) == BiometricAuthManager.BiometricStatus.AVAILABLE
        ) {
            BiometricAuthManager.promptBiometric(
                activity = activity,
                title = title,
                subtitle = "Biometric confirmation required for financial transactions",
                negativeButtonText = "Enter PIN Instead",
                onSuccess = {
                    pendingSensitiveAction?.invoke()
                    pendingSensitiveAction = null
                },
                onError = { _, _ ->
                    // Fallback to secure PIN dialog
                    showPinVerificationDialog.value = true
                },
                onFailed = {
                    showPinVerificationDialog.value = true
                }
            )
        } else {
            // Hardware doesn't support biometrics or disabled -> Fallback to secure PIN dialog
            showPinVerificationDialog.value = true
        }
    }

    fun verifyPinForSensitiveAction(enteredPin: String) {
        val user = currentUser.value ?: return
        val hash = com.example.data.security.SecurityUtils.hashPin(enteredPin, user.pinSalt)
        if (hash == user.pinHash) {
            showPinVerificationDialog.value = false
            pendingSensitiveAction?.invoke()
            pendingSensitiveAction = null
        } else {
            _authUiState.value = _authUiState.value.copy(
                errorMessage = "Invalid 4-digit PIN. Transaction authorization denied."
            )
        }
    }

    // --- Wallet Transactions ---

    fun executeSendMoney(recipientMobileOrWallet: String, recipientName: String, amount: Double, note: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "SEND",
                category = "Sent",
                amount = amount,
                fee = 0.0,
                recipientOrBiller = recipientMobileOrWallet,
                recipientTitle = recipientName,
                note = note
            )
            handleTransactionResult(result)
        }
    }

    fun executeBankTransfer(bankName: String, accountOrIban: String, recipientName: String, amount: Double, purpose: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "BANK_TRANSFER",
                category = "Bank Transfer",
                amount = amount,
                fee = 0.0, // Raast SBP fee-free rail
                recipientOrBiller = "$bankName: $accountOrIban",
                recipientTitle = recipientName,
                note = "Purpose: $purpose",
                providerRef = "RAAST-IBFT-" + (100000..999999).random()
            )
            handleTransactionResult(result)
        }
    }

    fun executeMobileRecharge(mobile: String, telcoNetwork: String, amount: Double, type: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "RECHARGE",
                category = "Recharge",
                amount = amount,
                fee = 0.0,
                recipientOrBiller = "$telcoNetwork ($mobile)",
                recipientTitle = "$telcoNetwork $type Top-Up",
                note = "Mobile Easyload to $mobile",
                providerRef = "TELCO-AGG-" + (100000..999999).random()
            )
            handleTransactionResult(result)
        }
    }

    fun executeBillPayment(billerName: String, consumerNumber: String, amount: Double) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "BILL_PAYMENT",
                category = "Bills",
                amount = amount,
                fee = 0.0,
                recipientOrBiller = "$billerName ($consumerNumber)",
                recipientTitle = "$billerName Utility Settlement",
                note = "1BILL Voucher: $consumerNumber",
                providerRef = "1BILL-" + (1000000..9999999).random()
            )
            handleTransactionResult(result)
        }
    }

    fun executeQrPayment(merchantName: String, merchantId: String, amount: Double) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "QR_PAYMENT",
                category = "QR Payment",
                amount = amount,
                fee = 0.0,
                recipientOrBiller = merchantId,
                recipientTitle = merchantName,
                note = "PayWave Merchant Dynamic QR",
                providerRef = "PW-QR-" + (100000..999999).random()
            )
            handleTransactionResult(result)
        }
    }

    fun executeAddMoney(paymentMethod: String, amount: Double) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "ADD_MONEY",
                category = "Deposit",
                amount = amount,
                fee = 0.0,
                recipientOrBiller = paymentMethod,
                recipientTitle = "Wallet Deposit via $paymentMethod",
                note = "Direct debit licensed gateway deposit",
                providerRef = "GATEWAY-" + (100000..999999).random()
            )
            handleTransactionResult(result)
        }
    }

    fun executeWithdraw(withdrawalMethod: String, amount: Double) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true, errorMessage = null)
            val result = walletRepo.executeTransaction(
                type = "WITHDRAW",
                category = "Withdraw",
                amount = amount,
                fee = 25.0, // Agent / ATM OTC token fee
                recipientOrBiller = withdrawalMethod,
                recipientTitle = "ATM / Agent Cash Withdrawal",
                note = "One-Time OTC Token Generated",
                providerRef = "ATM-OTC-" + (100000..999999).random()
            )
            handleTransactionResult(result)
        }
    }

    private fun handleTransactionResult(result: TransactionResult) {
        _authUiState.value = _authUiState.value.copy(isLoading = false)
        when (result) {
            is TransactionResult.Success -> {
                currentReceiptTx.value = result.transaction
                _authUiState.value = _authUiState.value.copy(
                    currentRoute = ScreenRoute.RECEIPT,
                    successMessage = "Transaction Ref: ${result.receiptRef} verified on ledger."
                )
            }
            is TransactionResult.Failed -> {
                _authUiState.value = _authUiState.value.copy(errorMessage = result.reason)
            }
        }
    }

    // --- KYC Submission & Admin Queue ---

    fun submitKycDocs(frontUri: String, backUri: String, selfieUri: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true)
            authRepo.submitKycDocuments(user.id, frontUri, backUri, selfieUri)
            _authUiState.value = _authUiState.value.copy(
                isLoading = false,
                currentRoute = ScreenRoute.HOME,
                successMessage = "CNIC and biometric liveness documents submitted for NADRA Verisys verification."
            )
        }
    }

    fun adminApproveKyc(userId: String, newStatus: String) {
        viewModelScope.launch {
            authRepo.updateKycStatusByAdmin(
                userId = userId,
                status = newStatus,
                notes = "Verified by Compliance Admin with NADRA Verisys identity check."
            )
            _authUiState.value = _authUiState.value.copy(
                successMessage = "KYC Status updated to $newStatus"
            )
        }
    }

    // --- Support & Tickets ---

    fun createSupportTicket(transactionRef: String?, category: String, subject: String, message: String) {
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true)
            val ticketId = walletRepo.submitDisputeTicket(transactionRef, category, subject, message)
            _authUiState.value = _authUiState.value.copy(
                isLoading = false,
                successMessage = "Support Ticket #$ticketId opened. Our compliance agent will review it shortly."
            )
        }
    }

    fun logoutOtherSessions() {
        viewModelScope.launch {
            authRepo.logoutAllOtherSessions()
            _authUiState.value = _authUiState.value.copy(
                successMessage = "All other device sessions have been terminated securely."
            )
        }
    }

    fun updatePin(oldPin: String, newPin: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            _authUiState.value = _authUiState.value.copy(isLoading = true)
            val ok = authRepo.changePin(user.id, oldPin, newPin)
            _authUiState.value = _authUiState.value.copy(
                isLoading = false,
                successMessage = if (ok) "Security PIN changed successfully!" else null,
                errorMessage = if (!ok) "Current PIN was incorrect." else null
            )
        }
    }

    fun toggleBiometricSetting(enabled: Boolean) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            authRepo.toggleBiometric(user.id, enabled)
        }
    }
}
