package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import com.example.ui.theme.PayWaveCyan
import com.example.ui.theme.PayWaveGold
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.theme.PayWaveGreenDark
import com.example.ui.theme.PayWaveNavyCard
import com.example.ui.theme.PayWaveNavyDark
import com.example.ui.theme.PayWaveSapphire
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusReversed
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.ScreenRoute
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PayWaveTopHeader(
    userName: String,
    kycStatus: String,
    unreadNotificationCount: Int,
    onNotificationsClick: () -> Unit,
    onKycBadgeClick: () -> Unit,
    onAdminClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Branded Logo Mark
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PayWave",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = " PK",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = PayWaveGreenBright
                        )
                    )
                }
                Text(
                    text = "Welcome, ${userName.ifBlank { "User" }}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // KYC Badge
            StatusBadge(
                status = kycStatus,
                modifier = Modifier
                    .clickable(onClick = onKycBadgeClick)
                    .testTag("kyc_badge_chip")
            )
            Spacer(modifier = Modifier.width(6.dp))

            // Admin button
            IconButton(
                onClick = onAdminClick,
                modifier = Modifier.testTag("admin_portal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Admin & Compliance",
                    tint = PayWaveGold
                )
            }

            // Notification Bell
            Box(contentAlignment = Alignment.TopEnd) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(StatusFailed)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PayWaveBalanceCard(
    wallet: WalletEntity?,
    onAddMoneyClick: () -> Unit,
    onSendMoneyClick: () -> Unit,
    onReceiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .testTag("wallet_balance_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0F394C),
                            Color(0xFF06232F),
                            PayWaveNavyDark
                        ),
                        radius = 900f
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(PayWaveGreenBright.copy(alpha = 0.5f), Color.Transparent)
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PayWaveGreenBright)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AVAILABLE BALANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PayWaveCyan,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = { isBalanceVisible = !isBalanceVisible },
                        modifier = Modifier.size(28.dp).testTag("toggle_balance_eye")
                    ) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Balance",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "PKR ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PayWaveGreenBright
                    )
                    Text(
                        text = if (isBalanceVisible) {
                            "%,.2f".format(wallet?.availableBalance ?: 0.0)
                        } else {
                            "••••••••"
                        },
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reconciled Ledger: PKR ${"%,.2f".format(wallet?.ledgerBalance ?: 0.0)}",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SBP Regulatory Limits Progress
                val dailySpent = wallet?.dailySpentAmount ?: 0.0
                val dailyLimit = wallet?.dailyLimit ?: 50000.0
                val limitRatio = (dailySpent / dailyLimit).toFloat().coerceIn(0f, 1f)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Limit Used",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "PKR ${"%,.0f".format(dailySpent)} / ${"%,.0f".format(dailyLimit)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (limitRatio > 0.8f) PayWaveGold else PayWaveGreenBright
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { limitRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (limitRatio > 0.8f) PayWaveGold else PayWaveGreenBright,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Bar inside Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSendMoneyClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("card_send_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = onAddMoneyClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("card_add_money_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Deposit", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    IconButton(
                        onClick = onReceiveClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .testTag("card_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Receive QR",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "VERIFIED", "SUCCESS" -> Triple(StatusSuccess.copy(alpha = 0.15f), StatusSuccess, if (status == "VERIFIED") "Verified" else "Success")
        "UNDER_REVIEW", "PENDING" -> Triple(StatusPending.copy(alpha = 0.15f), StatusPending, if (status == "UNDER_REVIEW") "Under Review" else "Pending")
        "NOT_SUBMITTED" -> Triple(Color.Gray.copy(alpha = 0.15f), Color.Gray, "Unverified")
        "REJECTED", "FAILED" -> Triple(StatusFailed.copy(alpha = 0.15f), StatusFailed, if (status == "REJECTED") "Rejected" else "Failed")
        "MANUAL_REVIEW" -> Triple(PayWaveCyan.copy(alpha = 0.15f), PayWaveCyan, "Manual Review")
        "REVERSED", "REFUNDED" -> Triple(StatusReversed.copy(alpha = 0.15f), StatusReversed, "Reversed")
        else -> Triple(Color.Gray.copy(alpha = 0.15f), Color.Gray, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit,
    badgeText: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
            if (badgeText != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PayWaveGold)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCredit = transaction.type == "RECEIVE" || transaction.type == "ADD_MONEY"
    val formattedDate = remember(transaction.timestamp) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(transaction.timestamp))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
            .testTag("tx_item_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isCredit) StatusSuccess.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (transaction.type) {
                            "SEND" -> Icons.Default.Send
                            "BANK_TRANSFER" -> Icons.Default.AccountBalance
                            "RECHARGE" -> Icons.Default.QrCode
                            "BILL_PAYMENT" -> Icons.Default.Receipt
                            "ADD_MONEY" -> Icons.Outlined.AccountBalanceWallet
                            "QR_PAYMENT" -> Icons.Default.QrCodeScanner
                            else -> Icons.Default.History
                        },
                        contentDescription = null,
                        tint = if (isCredit) StatusSuccess else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.recipientTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$formattedDate • ${transaction.category}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isCredit) "+ " else "- ") + "PKR " + "%,.2f".format(transaction.amount),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCredit) StatusSuccess else MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                StatusBadge(status = transaction.status)
            }
        }
    }
}

@Composable
fun PayWaveBottomNav(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("main_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.HOME,
            onClick = { onNavigate(ScreenRoute.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PayWaveNavyDark,
                indicatorColor = PayWaveGreenBright
            )
        )
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.SEND_MONEY,
            onClick = { onNavigate(ScreenRoute.SEND_MONEY) },
            icon = { Icon(Icons.Default.Send, contentDescription = "Send") },
            label = { Text("Send") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PayWaveNavyDark,
                indicatorColor = PayWaveGreenBright
            )
        )
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.SCAN_QR,
            onClick = { onNavigate(ScreenRoute.SCAN_QR) },
            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan QR") },
            label = { Text("Scan") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PayWaveNavyDark,
                indicatorColor = PayWaveGreenBright
            )
        )
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.TRANSACTIONS,
            onClick = { onNavigate(ScreenRoute.TRANSACTIONS) },
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PayWaveNavyDark,
                indicatorColor = PayWaveGreenBright
            )
        )
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.SETTINGS,
            onClick = { onNavigate(ScreenRoute.SETTINGS) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PayWaveNavyDark,
                indicatorColor = PayWaveGreenBright
            )
        )
    }
}

@Composable
fun PinAuthorizationDialog(
    title: String,
    onPinEntered: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PayWaveGreenBright
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Please enter your 4-digit security PIN to authorize this sensitive financial transaction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                // PIN dots visualizer
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    for (i in 0 until 4) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) PayWaveGreenBright
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Numeric keypad
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val rows = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("C", "0", "OK")
                    )

                    for (row in rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (btn in row) {
                                Button(
                                    onClick = {
                                        when (btn) {
                                            "C" -> enteredPin = ""
                                            "OK" -> {
                                                if (enteredPin.length == 4) {
                                                    onPinEntered(enteredPin)
                                                }
                                            }
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += btn
                                                    if (enteredPin.length == 4) {
                                                        onPinEntered(enteredPin)
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(60.dp, 48.dp)
                                        .testTag("pin_key_$btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (btn == "OK") PayWaveGreenBright
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (btn == "OK") Color.Black
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(
                                        text = btn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("cancel_pin_dialog")) {
                Text("Cancel")
            }
        }
    )
}
