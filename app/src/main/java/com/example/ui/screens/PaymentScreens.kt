package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Atm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.PayWaveBottomNav
import com.example.ui.theme.PayWaveCyan
import com.example.ui.theme.PayWaveGold
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.theme.PayWaveNavyCard
import com.example.ui.theme.PayWaveNavyDark
import com.example.ui.theme.PayWaveSapphire
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.PayWaveViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun SendMoneyScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wallet by viewModel.wallet.collectAsState()
    var recipient by remember { mutableStateOf("03014492018") }
    var recipientName by remember { mutableStateOf("Fatima Tariq") }
    var amountText by remember { mutableStateOf("2500") }
    var note by remember { mutableStateOf("Dinner settlement") }
    val uiState = viewModel.authUiState.value

    val quickAmounts = listOf(500, 1000, 2500, 5000, 10000)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.SEND_MONEY,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("send_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Money (P2P)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Balance:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "PKR ${"%,.2f".format(wallet?.availableBalance ?: 0.0)}",
                        fontWeight = FontWeight.Bold,
                        color = PayWaveGreenBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = recipient,
                        onValueChange = {
                            recipient = it
                            // Live account title lookup simulation
                            recipientName = if (it.endsWith("18")) "Fatima Tariq"
                            else if (it.endsWith("67")) "Asad Khan"
                            else "Verified PayWave PK Account"
                        },
                        label = { Text("Recipient Mobile / Wallet ID") },
                        placeholder = { Text("03XX XXXXXXX") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_recipient_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Verified Account Title
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PayWaveGreenBright.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PayWaveGreenBright, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Title: $recipientName",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PayWaveGreenBright
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Amount (PKR)") },
                        leadingIcon = {
                            Text(
                                text = "Rs.",
                                fontWeight = FontWeight.Bold,
                                color = PayWaveGreenBright,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fast amount chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickAmounts) { amt ->
                            Button(
                                onClick = { amountText = amt.toString() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("$amt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Purpose / Note (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_note_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Authorize Send PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeSendMoney(
                                    recipientMobileOrWallet = recipient,
                                    recipientName = recipientName,
                                    amount = amt,
                                    note = note
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("send_confirm_button"),
                        enabled = recipient.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Authorize & Send Money", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankTransferScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wallet by viewModel.wallet.collectAsState()
    val pakBanks = listOf(
        "Meezan Bank Ltd",
        "Habib Bank Limited (HBL)",
        "United Bank Limited (UBL)",
        "Bank Alfalah Ltd",
        "Standard Chartered PK",
        "MCB Bank Ltd",
        "Allied Bank Limited",
        "SadaPay",
        "NayaPay",
        "Easypaisa MFB",
        "JazzCash MFB"
    )

    var expanded by remember { mutableStateOf(false) }
    var selectedBank by remember { mutableStateOf(pakBanks.first()) }
    var accountOrIban by remember { mutableStateOf("01020304050607") }
    var recipientName by remember { mutableStateOf("Ali Raza") }
    var amountText by remember { mutableStateOf("5000") }
    var purpose by remember { mutableStateOf("Family Support / Raast") }
    val uiState = viewModel.authUiState.value

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.BANK_TRANSFER,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("bank_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Raast Bank Transfer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Instant 1LINK / SBP Raast Real-Time Rails (Fee: PKR 0.00)",
                        fontSize = 11.sp,
                        color = PayWaveGreenBright
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Bank selector dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedBank,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Destination Bank") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("bank_select_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            pakBanks.forEach { bank ->
                                DropdownMenuItem(
                                    text = { Text(bank) },
                                    onClick = {
                                        selectedBank = bank
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = accountOrIban,
                        onValueChange = {
                            accountOrIban = it
                            recipientName = if (it.length > 5) "Ali Raza (Verified Raast Title)" else ""
                        },
                        label = { Text("Account Number or IBAN (PK..)") },
                        placeholder = { Text("PK36MEZN0001020304050607") },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bank_account_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (recipientName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PayWaveGreenBright.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Account Title: $recipientName",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PayWaveGreenBright,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Transfer Amount (PKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bank_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Purpose of Payment") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bank_purpose_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Authorize Raast Transfer PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeBankTransfer(
                                    bankName = selectedBank,
                                    accountOrIban = accountOrIban,
                                    recipientName = recipientName,
                                    amount = amt,
                                    purpose = purpose
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("bank_confirm_button"),
                        enabled = accountOrIban.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Transfer via Raast IBFT", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MobileRechargeScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mobile by remember { mutableStateOf("03009876543") }
    var selectedTelco by remember { mutableStateOf("Jazz") }
    var rechargeType by remember { mutableStateOf("Prepaid") }
    var amountText by remember { mutableStateOf("500") }
    val uiState = viewModel.authUiState.value

    val telcos = listOf("Jazz", "Zong", "Telenor", "Ufone")
    val amounts = listOf(150, 250, 500, 1000, 2000)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.MOBILE_RECHARGE,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("recharge_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mobile Easyload & Top-Up",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Select Network Operator", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        telcos.forEach { telco ->
                            val isSelected = selectedTelco == telco
                            Button(
                                onClick = { selectedTelco = telco },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("telco_btn_$telco"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) PayWaveGreenBright else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(telco, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Recipient Mobile (03XX XXXXXXX)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharge_mobile_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recharge type toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Prepaid", "Postpaid", "Super Card").forEach { type ->
                            val selected = rechargeType == type
                            Button(
                                onClick = { rechargeType = type },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) PayWaveSapphire else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(type, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Recharge Amount (PKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("recharge_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(amounts) { amt ->
                            Button(
                                onClick = { amountText = amt.toString() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Rs. $amt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Confirm $selectedTelco Load PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeMobileRecharge(
                                    mobile = mobile,
                                    telcoNetwork = selectedTelco,
                                    amount = amt,
                                    type = rechargeType
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("recharge_submit_button"),
                        enabled = mobile.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Text("Confirm Easyload", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillPaymentScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Electricity") }
    var selectedBiller by remember { mutableStateOf("LESCO (Lahore Electric)") }
    var consumerNumber by remember { mutableStateOf("08112233445566U") }
    var billAmount by remember { mutableStateOf("7840") }
    var isBillInquired by remember { mutableStateOf(true) }
    val uiState = viewModel.authUiState.value

    val categories = listOf("Electricity", "Gas", "Water", "Internet")
    val billers = when (selectedCategory) {
        "Electricity" -> listOf("LESCO (Lahore Electric)", "K-Electric Karachi", "IESCO Islamabad", "FESCO Faisalabad")
        "Gas" -> listOf("SNGPL (Sui Northern)", "SSGC (Sui Southern)")
        "Water" -> listOf("WASA Lahore", "KWSB Karachi", "WASA Rawalpindi")
        else -> listOf("PTCL Broadband", "Nayatel Fiber", "StormFiber")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.BILL_PAYMENT,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("bill_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "1LINK 1BILL Settlement",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "State Bank Approved Utility Billing Aggregator",
                        fontSize = 11.sp,
                        color = PayWaveCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Button(
                        onClick = {
                            selectedCategory = cat
                            selectedBiller = when (cat) {
                                "Electricity" -> "LESCO (Lahore Electric)"
                                "Gas" -> "SNGPL (Sui Northern)"
                                "Water" -> "WASA Lahore"
                                else -> "PTCL Broadband"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PayWaveGreenBright else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Selected Company: $selectedBiller", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = consumerNumber,
                        onValueChange = {
                            consumerNumber = it
                            isBillInquired = it.length > 8
                        },
                        label = { Text("14-Digit Consumer / Ref Number") },
                        placeholder = { Text("08112233445566U") },
                        leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bill_consumer_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isBillInquired) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = PayWaveNavyCard),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Consumer Title:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("Muhammad Tariq Khan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Due Date:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("28 Sep 2026", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PayWaveGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Status:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("UNPAID", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFF5252))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Payable Within Due Date:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("PKR $billAmount", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = PayWaveGreenBright)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = billAmount.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Authorize 1BILL Payment PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeBillPayment(
                                    billerName = selectedBiller,
                                    consumerNumber = consumerNumber,
                                    amount = amt
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("bill_pay_button"),
                        enabled = consumerNumber.isNotBlank() && isBillInquired && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Text("Pay Bill via 1BILL", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddMoneyScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("Debit / Credit Card (3D Secure)") }
    var amountText by remember { mutableStateOf("10000") }
    val uiState = viewModel.authUiState.value

    val methods = listOf(
        "Debit / Credit Card (3D Secure)",
        "1LINK 1BILL Top-up Voucher",
        "Direct Bank Debit (1LINK Raast)"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.ADD_MONEY,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("add_money_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deposit Funds",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Select Payment Rails", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedMethod = method },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PayWaveGreenBright.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = if (isSelected) PayWaveGreenBright else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = method,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Deposit Amount (PKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            viewModel.executeAddMoney(selectedMethod, amt)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("add_money_submit_button"),
                        enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Text("Proceed with Secure Deposit", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WithdrawScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedChannel by remember { mutableStateOf("1LINK ATM Cardless Withdrawal") }
    var amountText by remember { mutableStateOf("3000") }
    val uiState = viewModel.authUiState.value

    val channels = listOf(
        "1LINK ATM Cardless Withdrawal",
        "Authorized PayWave Partner OTC Agent"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.WITHDRAW,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("withdraw_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cash Out / Withdrawal",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Withdrawal Rail", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    channels.forEach { channel ->
                        val isSelected = selectedChannel == channel
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedChannel = channel },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) PayWaveGreenBright.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Atm,
                                    contentDescription = null,
                                    tint = if (isSelected) PayWaveGreenBright else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = channel,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Withdrawal Amount (PKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1LINK ATM Fee: PKR 25.00 inclusive of FED",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Generate Cash Withdrawal Token PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeWithdraw(selectedChannel, amt)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("withdraw_submit_button"),
                        enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Text("Generate One-Time OTC Token", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScanQrScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var merchantCode by remember { mutableStateOf("MERCHANT-KARACHI-84920") }
    var amountText by remember { mutableStateOf("1450") }
    val uiState = viewModel.authUiState.value

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.SCAN_QR,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("scan_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan Merchant QR",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Simulated Viewfinder Frame
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.Black)
                    .border(2.dp, PayWaveGreenBright, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = PayWaveGreenBright.copy(alpha = 0.8f),
                        modifier = Modifier.size(140.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Align Merchant QR within Frame",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Identified Merchant", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Agha's Supermarket Karachi (Verified Merchant)",
                        fontSize = 13.sp,
                        color = PayWaveGreenBright,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = merchantCode,
                        onValueChange = { merchantCode = it },
                        label = { Text("Merchant / Terminal ID") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qr_merchant_code_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                        label = { Text("Payment Amount (PKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("qr_amount_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            val act = context as? FragmentActivity
                            viewModel.requestSensitiveActionAuthorization(
                                activity = act,
                                title = "Authorize QR Pay PKR ${"%,.2f".format(amt)}"
                            ) {
                                viewModel.executeQrPayment(
                                    merchantName = "Agha's Supermarket",
                                    merchantId = merchantCode,
                                    amount = amt
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("qr_pay_button"),
                        enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0 && !uiState.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PayWaveGreenBright,
                            contentColor = Color(0xFF003822)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                        } else {
                            Text("Authorize QR Payment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiveQrScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var requestedAmount by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            PayWaveBottomNav(
                currentRoute = ScreenRoute.RECEIVE_QR,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                    modifier = Modifier.testTag("receive_qr_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Receive Money QR",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentUser?.fullName ?: "Fawaz Ahmad",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Raast Alias: ${currentUser?.mobileNumber ?: "03001234567"}",
                        fontSize = 13.sp,
                        color = PayWaveGreenBright,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // QR Code graphic
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "Dynamic QR Code",
                            tint = Color.Black,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (requestedAmount.isNotBlank()) {
                        Text(
                            text = "Amount: PKR ${"%,.2f".format(requestedAmount.toDoubleOrNull() ?: 0.0)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PayWaveGreenBright
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = "Scan with any Raast or 1LINK compliant banking app in Pakistan to transfer funds instantly.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = requestedAmount,
                onValueChange = { if (it.all { ch -> ch.isDigit() }) requestedAmount = it },
                label = { Text("Set Request Amount (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("receive_amount_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.navigateTo(ScreenRoute.HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("done_receive_qr_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PayWaveGreenBright,
                    contentColor = Color(0xFF003822)
                )
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
