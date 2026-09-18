package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.theme.PayWaveGold
import com.example.ui.viewmodel.PayWaveViewModel
import com.example.ui.viewmodel.ScreenRoute

data class FreeFirePackage(
    val id: String,
    val title: String,
    val diamonds: Int,
    val price: Double,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeFireVoucherScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()
    val authState by viewModel.authUiState.collectAsState()
    val context = LocalContext.current

    var playerUid by remember { mutableStateOf("") }
    var selectedPackage by remember {
        mutableStateOf(
            FreeFirePackage("pkg_1", "100 Diamonds + 10 Bonus", 110, 250.0, true)
        )
    }

    val packages = listOf(
        FreeFirePackage("pkg_1", "100 Diamonds + 10 Bonus", 110, 250.0, true),
        FreeFirePackage("pkg_2", "310 Diamonds + 30 Bonus", 340, 750.0),
        FreeFirePackage("pkg_3", "520 Diamonds + 60 Bonus", 580, 1200.0),
        FreeFirePackage("pkg_4", "Weekly Membership Pass", 450, 450.0),
        FreeFirePackage("pkg_5", "Level Up Pass (Top Seller)", 800, 650.0)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Free Fire Voucher Store", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.HOME) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Banner Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFB300))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsEsports,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Garena Free Fire Official",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Instant PIN Code Generation & Ledger Verified",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Available Wallet Balance: PKR ${"%,.2f".format(wallet?.availableBalance ?: 0.0)}",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Player ID Input
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Free Fire Player UID",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedTextField(
                        value = playerUid,
                        onValueChange = { playerUid = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ff_player_uid_input"),
                        placeholder = { Text("e.g. 5829104829") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    Text(
                        text = "Your digital voucher PIN will be instantly issued and recorded on the immutable ledger.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Text(
                    text = "Select Voucher Package",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(packages) { pkg ->
                val isSelected = selectedPackage.id == pkg.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPackage = pkg }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) PayWaveGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PayWaveGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPackage = pkg }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pkg.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (pkg.isPopular) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PayWaveGold)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("POPULAR", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                                Text(
                                    text = "${pkg.diamonds} Diamonds credited instantly",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "PKR ${"%,.0f".format(pkg.price)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = PayWaveGreenBright
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (playerUid.isBlank()) {
                            viewModel.showSuccess("Please enter your Free Fire Player UID first.")
                            return@Button
                        }
                        viewModel.executeFreeFireVoucherPurchase(
                            packageTitle = selectedPackage.title,
                            diamonds = selectedPackage.diamonds,
                            price = selectedPackage.price,
                            playerUid = playerUid.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("buy_ff_voucher_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PayWaveGreenBright)
                ) {
                    Text(
                        text = "Purchase Voucher & Get PIN (PKR ${"%,.0f".format(selectedPackage.price)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
