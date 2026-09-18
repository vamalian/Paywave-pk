package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Atm
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PayWaveBalanceCard
import com.example.ui.components.PayWaveBottomNav
import com.example.ui.components.PayWaveTopHeader
import com.example.ui.components.QuickActionItem
import com.example.ui.components.TransactionRowItem
import com.example.ui.theme.PayWaveCyan
import com.example.ui.theme.PayWaveGold
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.viewmodel.PayWaveViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun HomeScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = My Wallet, 1 = Admin Dashboard
    val currentUser by viewModel.currentUser.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val beneficiaries by viewModel.beneficiaries.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs = notifications.count { !it.isRead }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.HOME,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Top Header
            PayWaveTopHeader(
                userName = currentUser?.fullName ?: "Fawaz Ahmad",
                kycStatus = currentUser?.kycStatus ?: "NOT_SUBMITTED",
                unreadNotificationCount = unreadNotifs,
                onNotificationsClick = { viewModel.navigateTo(ScreenRoute.SETTINGS) },
                onKycBadgeClick = { viewModel.navigateTo(ScreenRoute.KYC_VERIFY) },
                onAdminClick = { selectedMainTab = 1 }
            )

            // Main Tab Switcher (My Wallet vs Admin Dashboard directly on Home Screen)
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = { Text("My Wallet", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = { Text("Admin Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedMainTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("home_screen_scroll")
                ) {
                    // 2. Primary Wallet Balance Card
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        PayWaveBalanceCard(
                            wallet = wallet,
                            onAddMoneyClick = { viewModel.navigateTo(ScreenRoute.ADD_MONEY) },
                            onSendMoneyClick = { viewModel.navigateTo(ScreenRoute.SEND_MONEY) },
                            onReceiveClick = { viewModel.navigateTo(ScreenRoute.RECEIVE_QR) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. KYC Warning Banner if not verified
                    if (currentUser?.kycStatus != "VERIFIED") {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .clickable { viewModel.navigateTo(ScreenRoute.KYC_VERIFY) }
                                    .testTag("home_kyc_prompt_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = PayWaveGold.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = PayWaveGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (currentUser?.kycStatus == "UNDER_REVIEW") "KYC Verification Under Review"
                                            else "Complete CNIC & Biometric Verification",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Authorized NADRA Verisys identity verification unlocks up to PKR 200,000 monthly limit.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = PayWaveGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // 4. Frequent Beneficiaries Row
                    if (beneficiaries.isNotEmpty()) {
                        item {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Quick Send",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    item { Spacer(modifier = Modifier.width(8.dp)) }
                                    items(beneficiaries) { ben ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(14.dp))
                                                .clickable { viewModel.navigateTo(ScreenRoute.SEND_MONEY) }
                                                .padding(4.dp)
                                                .width(68.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (ben.type == "WALLET") PayWaveGreenBright.copy(alpha = 0.15f)
                                                        else PayWaveCyan.copy(alpha = 0.15f)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = ben.name.take(2).uppercase(),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = if (ben.type == "WALLET") PayWaveGreenBright else PayWaveCyan
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = ben.name,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    item { Spacer(modifier = Modifier.width(8.dp)) }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // 5. Quick Actions Grid
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Financial Services",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Row 1
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    QuickActionItem(
                                        icon = Icons.Default.Send,
                                        label = "Send Money",
                                        tag = "action_send_money",
                                        onClick = { viewModel.navigateTo(ScreenRoute.SEND_MONEY) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.AccountBalance,
                                        label = "Bank Transfer",
                                        tag = "action_bank_transfer",
                                        badgeText = "Raast",
                                        onClick = { viewModel.navigateTo(ScreenRoute.BANK_TRANSFER) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.Smartphone,
                                        label = "Recharge",
                                        tag = "action_recharge",
                                        onClick = { viewModel.navigateTo(ScreenRoute.MOBILE_RECHARGE) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.Receipt,
                                        label = "Bill Payments",
                                        tag = "action_bill_payment",
                                        badgeText = "1BILL",
                                        onClick = { viewModel.navigateTo(ScreenRoute.BILL_PAYMENT) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Row 2
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    QuickActionItem(
                                        icon = Icons.Default.QrCodeScanner,
                                        label = "Scan QR",
                                        tag = "action_scan_qr",
                                        onClick = { viewModel.navigateTo(ScreenRoute.SCAN_QR) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.QrCode,
                                        label = "Receive QR",
                                        tag = "action_receive_qr",
                                        onClick = { viewModel.navigateTo(ScreenRoute.RECEIVE_QR) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.AddCard,
                                        label = "Add Money",
                                        tag = "action_add_money",
                                        onClick = { viewModel.navigateTo(ScreenRoute.ADD_MONEY) }
                                    )
                                    QuickActionItem(
                                        icon = Icons.Default.Atm,
                                        label = "Withdraw",
                                        tag = "action_withdraw",
                                        onClick = { viewModel.navigateTo(ScreenRoute.WITHDRAW) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // 6. Recent Transactions
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            TextButton(
                                onClick = { viewModel.navigateTo(ScreenRoute.TRANSACTIONS) },
                                modifier = Modifier.testTag("view_all_transactions_button")
                            ) {
                                Text("View All", color = PayWaveGreenBright, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (transactions.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions recorded yet.\nSend money or top-up to begin.",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        items(transactions.take(5)) { tx ->
                            TransactionRowItem(
                                transaction = tx,
                                onClick = {
                                    viewModel.currentReceiptTx.value = tx
                                    viewModel.navigateTo(ScreenRoute.RECEIPT)
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    AdminDashboardContent(viewModel = viewModel)
                }
            }
        }
    }
}
