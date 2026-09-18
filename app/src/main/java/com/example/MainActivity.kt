package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PinAuthorizationDialog
import com.example.ui.screens.AddMoneyScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BankTransferScreen
import com.example.ui.screens.BillPaymentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KycVerificationScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MobileRechargeScreen
import com.example.ui.screens.OtpVerificationScreen
import com.example.ui.screens.ReceiptScreen
import com.example.ui.screens.ReceiveQrScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.ScanQrScreen
import com.example.ui.screens.SendMoneyScreen
import com.example.ui.screens.SetPinScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SupportAndDisputeScreen
import com.example.ui.screens.TransactionHistoryScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.theme.PayWaveNavyDark
import com.example.ui.theme.PayWavePKTheme
import com.example.ui.theme.PayWaveSapphire
import com.example.ui.viewmodel.PayWaveViewModel
import com.example.ui.viewmodel.ScreenRoute

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PayWavePKTheme {
                val viewModel: PayWaveViewModel = viewModel()
                PayWaveApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PayWaveApp(viewModel: PayWaveViewModel) {
    val authUiState by viewModel.authUiState.collectAsState()
    val showPinDialog by viewModel.showPinVerificationDialog.collectAsState()
    val sensitiveTitle by viewModel.sensitiveActionTitle.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authUiState.errorMessage, authUiState.successMessage) {
        val msg = authUiState.errorMessage ?: authUiState.successMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (authUiState.currentRoute) {
                ScreenRoute.SPLASH -> SplashScreen()
                ScreenRoute.LOGIN -> LoginScreen(viewModel = viewModel)
                ScreenRoute.REGISTER -> RegisterScreen(viewModel = viewModel)
                ScreenRoute.OTP_VERIFY -> OtpVerificationScreen(viewModel = viewModel)
                ScreenRoute.SET_PIN -> SetPinScreen(viewModel = viewModel)
                ScreenRoute.HOME -> HomeScreen(viewModel = viewModel)
                ScreenRoute.SEND_MONEY -> SendMoneyScreen(viewModel = viewModel)
                ScreenRoute.BANK_TRANSFER -> BankTransferScreen(viewModel = viewModel)
                ScreenRoute.MOBILE_RECHARGE -> MobileRechargeScreen(viewModel = viewModel)
                ScreenRoute.BILL_PAYMENT -> BillPaymentScreen(viewModel = viewModel)
                ScreenRoute.ADD_MONEY -> AddMoneyScreen(viewModel = viewModel)
                ScreenRoute.WITHDRAW -> WithdrawScreen(viewModel = viewModel)
                ScreenRoute.SCAN_QR -> ScanQrScreen(viewModel = viewModel)
                ScreenRoute.RECEIVE_QR -> ReceiveQrScreen(viewModel = viewModel)
                ScreenRoute.TRANSACTIONS -> TransactionHistoryScreen(viewModel = viewModel)
                ScreenRoute.RECEIPT -> ReceiptScreen(viewModel = viewModel)
                ScreenRoute.KYC_VERIFY -> KycVerificationScreen(viewModel = viewModel)
                ScreenRoute.ADMIN_DASHBOARD -> AdminDashboardScreen(viewModel = viewModel)
                ScreenRoute.SETTINGS -> SettingsScreen(viewModel = viewModel)
                ScreenRoute.SUPPORT -> SupportAndDisputeScreen(viewModel = viewModel)
            }

            // High-Security 4-digit PIN Authorization Dialog
            if (showPinDialog) {
                PinAuthorizationDialog(
                    title = sensitiveTitle,
                    onPinEntered = { pin ->
                        viewModel.verifyPinForSensitiveAction(pin)
                    },
                    onDismiss = {
                        viewModel.showPinVerificationDialog.value = false
                        viewModel.pendingSensitiveAction = null
                    }
                )
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PayWaveNavyDark, Color(0xFF041822))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(PayWaveGreenBright, PayWaveSapphire)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "PayWave Logo",
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "PayWave PK",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Biometric Digital Wallet & Payments",
                fontSize = 13.sp,
                color = PayWaveGreenBright
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = PayWaveGreenBright,
                strokeWidth = 3.dp
            )
        }
    }
}
