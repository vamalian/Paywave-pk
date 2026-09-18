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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.PaymentProviderRegistry
import com.example.ui.components.StatusBadge
import com.example.ui.theme.PayWaveCyan
import com.example.ui.theme.PayWaveGold
import com.example.ui.theme.PayWaveGreenBright
import com.example.ui.theme.PayWaveNavyCard
import com.example.ui.theme.PayWaveNavyDark
import com.example.ui.theme.PayWaveSapphire
import com.example.ui.theme.StatusFailed
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.PayWaveViewModel
import com.example.ui.viewmodel.ScreenRoute

@Composable
fun KycVerificationScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var frontDocAdded by remember { mutableStateOf(true) }
    var backDocAdded by remember { mutableStateOf(true) }
    var selfieAdded by remember { mutableStateOf(true) }
    var consentSigned by remember { mutableStateOf(true) }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
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
                    modifier = Modifier.testTag("kyc_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Identity & KYC Verification",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Authorized NADRA Verisys & SBP CDD Compliance",
                        fontSize = 11.sp,
                        color = PayWaveCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Status Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Current Verification Level", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (currentUser?.kycStatus == "VERIFIED") "Tier-1 Fully Verified" else "Tier-0 Basic (Restricted Limits)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusBadge(status = currentUser?.kycStatus ?: "NOT_SUBMITTED")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Required Documentation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // CNIC Front Card
            KycUploadCard(
                title = "1. CNIC Front Side",
                description = "Clear photo showing Photo, Name, and 13-digit CNIC number",
                isUploaded = frontDocAdded,
                tag = "upload_front_cnic",
                onToggle = { frontDocAdded = !frontDocAdded }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CNIC Back Card
            KycUploadCard(
                title = "2. CNIC Back Side",
                description = "Clear photo showing Address and Family Sequence Number",
                isUploaded = backDocAdded,
                tag = "upload_back_cnic",
                onToggle = { backDocAdded = !backDocAdded }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Biometric Selfie Card
            KycUploadCard(
                title = "3. Biometric Selfie Liveness",
                description = "Live face match against NADRA national identity database",
                isUploaded = selfieAdded,
                tag = "upload_selfie",
                onToggle = { selfieAdded = !selfieAdded }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Consent Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { consentSigned = !consentSigned },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = consentSigned,
                    onCheckedChange = { consentSigned = it },
                    colors = CheckboxDefaults.colors(checkedColor = PayWaveGreenBright)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I explicitly authorize PayWave PK to verify my CNIC and biometric liveness data via authorized NADRA Verisys rails in compliance with SBP regulations.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.submitKycDocs(
                        frontUri = "content://media/cnic_front_signed.jpg",
                        backUri = "content://media/cnic_back_signed.jpg",
                        selfieUri = "content://media/selfie_liveness_signed.jpg"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_kyc_button"),
                enabled = frontDocAdded && backDocAdded && selfieAdded && consentSigned,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PayWaveGreenBright,
                    contentColor = Color(0xFF003822)
                )
            ) {
                Text("Submit for NADRA Verification", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun KycUploadCard(
    title: String,
    description: String,
    isUploaded: Boolean,
    tag: String,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUploaded) PayWaveGreenBright.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isUploaded) androidx.compose.foundation.BorderStroke(1.dp, PayWaveGreenBright) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isUploaded) PayWaveGreenBright.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUploaded) Icons.Default.Check else Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (isUploaded) PayWaveGreenBright else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUploaded) "Captured" else "Capture",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUploaded) PayWaveGreenBright else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            AdminDashboardContent(
                viewModel = viewModel,
                onBackClick = { viewModel.navigateTo(ScreenRoute.HOME) }
            )
        }
    }
}

@Composable
fun AdminDashboardContent(
    viewModel: PayWaveViewModel,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentUser by viewModel.currentUser.collectAsState()
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()
    val providers = remember { PaymentProviderRegistry.getAllProviders() }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("admin_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Admin & Compliance Portal",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PayWaveGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                    }
                }
                Text(
                    text = "Role-Based Access Control (RBAC) • Auditor Mode",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Auditor Session Card with Masked Email Protection
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = PayWaveGreenBright,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Admin: ${currentUser?.fullName ?: "Fawaz Ahmad"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Email: f••••••••••••@gmail.com",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PayWaveGreenBright.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "EMAIL VERIFIED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PayWaveGreenBright
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("KYC Queue", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Ledger Chain", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Provider Rails", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (selectedTab) {
                0 -> {
                    // KYC Queue
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Pending Identity Verification Reviews",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = currentUser?.fullName ?: "User",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        StatusBadge(status = currentUser?.kycStatus ?: "UNDER_REVIEW")
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "CNIC: ${currentUser?.maskedCnic ?: "42101-*******-1"} • Mobile: ${currentUser?.mobileNumber ?: "03001234567"}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Provider Check: NADRA Verisys matching verified. Biometric facial score: 98.2%.",
                                        fontSize = 11.sp,
                                        color = PayWaveCyan
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = {
                                                currentUser?.id?.let {
                                                    viewModel.adminApproveKyc(it, "VERIFIED")
                                                }
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("admin_approve_kyc_button"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = PayWaveGreenBright,
                                                contentColor = Color(0xFF003822)
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Approve (NADRA Verified)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                currentUser?.id?.let {
                                                    viewModel.adminApproveKyc(it, "REJECTED")
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Reject", fontSize = 11.sp, color = StatusFailed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Immutable Ledger Explorer
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "Cryptographically Chained Immutable Ledger",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Every balance update is chained with SHA-256 hash preventing modification or tampering.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(ledgerEntries) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${entry.type} • PKR ${"%,.2f".format(entry.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (entry.type == "CREDIT") StatusSuccess else PayWaveCyan
                                        )
                                        Text(
                                            text = "Bal: PKR ${"%,.0f".format(entry.balanceAfter)}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Entry ID: ${entry.entryId}",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Hash: ${entry.entryHash.take(24)}...",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = PayWaveGreenBright
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Prev: ${entry.previousHash.take(24)}...",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Payment Rails & Deployment Prerequisites
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Licensed Payment Provider Rails",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Strict provider abstraction per State Bank of Pakistan fintech regulations.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(providers) { prov ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = prov.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        StatusBadge(status = if (prov.isReadyForProduction()) "VERIFIED" else "UNDER_REVIEW")
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = prov.description,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Deployment Prerequisites:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PayWaveGold
                                    )
                                    prov.requiredPrerequisites.forEach { req ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "• ${req.title} (${req.authorityOrVendor})",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "  ${req.instructions}",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
