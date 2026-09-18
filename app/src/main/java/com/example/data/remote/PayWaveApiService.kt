package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PayWaveApiService {

    // 1. Authentication
    @POST("/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterResponse>>

    @POST("/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<ApiResponse<VerifyOtpResponse>>

    @POST("/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthTokenResponse>>

    @POST("/auth/refresh")
    suspend fun refreshAuthToken(
        @Header("Authorization") refreshToken: String
    ): Response<ApiResponse<AuthTokenResponse>>

    // 2. KYC Verification
    @POST("/kyc/submit")
    suspend fun submitKyc(
        @Header("Authorization") token: String,
        @Body request: KycSubmitRequest
    ): Response<ApiResponse<KycStatusResponse>>

    @GET("/kyc/status")
    suspend fun getKycStatus(
        @Header("Authorization") token: String
    ): Response<ApiResponse<KycStatusResponse>>

    // 3. Wallet Operations
    @GET("/wallet")
    suspend fun getWalletDetails(
        @Header("Authorization") token: String
    ): Response<ApiResponse<WalletBalanceDto>>

    @POST("/wallet/deposit")
    suspend fun depositFunds(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: Map<String, Any>
    ): Response<ApiResponse<TransactionResultDto>>

    @POST("/wallet/transfer")
    suspend fun transferMoney(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: TransferRequest
    ): Response<ApiResponse<TransactionResultDto>>

    @POST("/wallet/withdraw")
    suspend fun withdrawFunds(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: Map<String, Any>
    ): Response<ApiResponse<TransactionResultDto>>

    // 4. Payments
    @POST("/payments/qr")
    suspend fun processQrPayment(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: QrPaymentRequest
    ): Response<ApiResponse<TransactionResultDto>>

    @POST("/payments/recharge")
    suspend fun processMobileRecharge(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: RechargeRequest
    ): Response<ApiResponse<TransactionResultDto>>

    @POST("/payments/bills")
    suspend fun payBill(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: BillPaymentRequest
    ): Response<ApiResponse<TransactionResultDto>>

    @POST("/payments/bank-transfer")
    suspend fun transferToBank(
        @Header("Authorization") token: String,
        @Header("X-Idempotency-Key") idempotencyKey: String,
        @Body request: BankTransferRequest
    ): Response<ApiResponse<TransactionResultDto>>

    // 5. Transactions
    @GET("/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<TransactionResultDto>>>

    @GET("/transactions/{id}")
    suspend fun getTransactionById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<TransactionResultDto>>
}
