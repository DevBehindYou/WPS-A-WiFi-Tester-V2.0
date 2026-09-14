package com.wpsa.tester.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wpsa.tester.root.RootState
import com.wpsa.tester.ui.components.WpsDialog
import com.wpsa.tester.ui.components.WpsMethodSheet
import com.wpsa.tester.ui.components.WpsPinDialog
import com.wpsa.tester.ui.theme.Dimensions
import com.wpsa.tester.ui.theme.MonospaceSmall
import com.wpsa.tester.ui.theme.StatusError
import com.wpsa.tester.ui.theme.StatusSuccess
import com.wpsa.tester.ui.theme.StatusWarning
import com.wpsa.tester.ui.viewmodels.ScannerViewModel
import com.wpsa.tester.wifi.WifiNetwork

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Trigger initial scan when root is confirmed granted
    LaunchedEffect(uiState.rootStatus) {
        if (uiState.rootStatus == RootState.GRANTED && uiState.networks.isEmpty()) {
            viewModel.scanNetworks()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.WifiTethering,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "WPS/A Tester",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.scanNetworks() },
                        enabled = !uiState.isScanning
                    ) {
                        if (uiState.isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Scan")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Device Readiness Summary Card (§21)
            DeviceReadinessCard(
                rootStatus = uiState.rootStatus,
                isWifiEnabled = uiState.isWifiEnabled,
                activeInterface = uiState.activeInterface
            )

            // Warning: Wi-Fi Disabled Banner
            AnimatedVisibility(visible = !uiState.isWifiEnabled) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.screenPadding, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Column {
                                Text("Wi-Fi is turned off", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Enable Wi-Fi to scan nearby networks.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Turn On")
                        }
                    }
                }
            }

            // Warning: Location Disabled Banner (Required for Android 10-14 scan results)
            AnimatedVisibility(visible = !uiState.isLocationEnabled && uiState.isWifiEnabled) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.screenPadding, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocationOff, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                            Column {
                                Text("Location Services Off", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("Android throttles Wi-Fi scanning when location is off.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Button(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        ) {
                            Text("Enable")
                        }
                    }
                }
            }

            // Network Scanner Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.screenPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nearby Networks (${uiState.networks.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.isScanning) {
                    Text(
                        text = "Scanning…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Networks List
            if (uiState.networks.isEmpty() && !uiState.isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.WifiFind,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "No Wi-Fi Networks Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ensure Wi-Fi and Location are enabled, then tap Scan to search for nearby access points.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.scanNetworks() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Scan Networks")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimensions.screenPadding,
                        end = Dimensions.screenPadding,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimensions.listSpacing)
                ) {
                    items(
                        items = uiState.networks,
                        key = { it.bssid }
                    ) { network ->
                        NetworkCard(
                            network = network,
                            onCardClick = { viewModel.openMethodSheet(network) },
                            onConnectPbc = { viewModel.selectNetworkForPbc(network) },
                            onConnectPin = { viewModel.openPinDialog(network) }
                        )
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for WPS Connection Method Selection
    uiState.selectedNetworkForSheet?.let { network ->
        WpsMethodSheet(
            network = network,
            onDismiss = { viewModel.dismissMethodSheet() },
            onSelectPbc = { viewModel.selectNetworkForPbc(it) },
            onSelectPin = { viewModel.openPinDialog(it) }
        )
    }

    // Active WPS Progression Dialog
    if (uiState.showWpsProgressDialog && uiState.targetNetwork != null) {
        WpsDialog(
            state = uiState.wpsState,
            networkName = uiState.targetNetwork!!.ssid.ifBlank { uiState.targetNetwork!!.bssid },
            onCancel = { viewModel.cancelActiveWps() },
            onDismiss = { viewModel.dismissWpsProgressDialog() }
        )
    }

    // WPS PIN Entry Dialog
    if (uiState.showPinDialog && uiState.targetNetwork != null) {
        WpsPinDialog(
            network = uiState.targetNetwork!!,
            onConnectPin = { pin -> viewModel.confirmPinConnection(pin) },
            onDismiss = { viewModel.dismissPinDialog() },
            validatePin = { viewModel.validatePin(it) },
            validateChecksum = { viewModel.isChecksumValid(it) }
        )
    }
}

@Composable
fun DeviceReadinessCard(
    rootStatus: RootState,
    isWifiEnabled: Boolean,
    activeInterface: String?
) {
    Card(
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.screenPadding, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Readiness",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (activeInterface != null) "Iface: $activeInterface" else "Iface: Auto",
                    style = MonospaceSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Root Status
                val (rootColor, rootText) = when (rootStatus) {
                    RootState.GRANTED -> Pair(StatusSuccess, "Root Granted")
                    RootState.DENIED -> Pair(StatusError, "Root Denied")
                    RootState.CHECKING -> Pair(StatusWarning, "Checking Root")
                    else -> Pair(MaterialTheme.colorScheme.outline, "Unavailable")
                }
                StatusIndicatorItem(label = "Root", value = rootText, color = rootColor)

                // Wi-Fi Status
                val (wifiColor, wifiText) = if (isWifiEnabled) {
                    Pair(StatusSuccess, "Wi-Fi Active")
                } else {
                    Pair(StatusError, "Wi-Fi Off")
                }
                StatusIndicatorItem(label = "Wi-Fi", value = wifiText, color = wifiColor)

                // WPS Stack
                StatusIndicatorItem(
                    label = "WPS Stack",
                    value = if (rootStatus == RootState.GRANTED) "Ready" else "Limited",
                    color = if (rootStatus == RootState.GRANTED) StatusSuccess else StatusWarning
                )
            }
        }
    }
}

@Composable
fun StatusIndicatorItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NetworkCard(
    network: WifiNetwork,
    onCardClick: () -> Unit,
    onConnectPbc: () -> Unit,
    onConnectPin: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(Dimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Top Row: Signal Icon, SSID, and RSSI
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val signalColor = when (network.signalStrength) {
                    "Excellent" -> StatusSuccess
                    "Good" -> MaterialTheme.colorScheme.primary
                    "Fair" -> StatusWarning
                    else -> MaterialTheme.colorScheme.error
                }

                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Signal ${network.signalStrength}",
                    tint = signalColor,
                    modifier = Modifier.size(24.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = network.ssid.ifBlank { "<Hidden SSID>" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = network.bssid,
                        style = MonospaceSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${network.rssi} dBm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            // Badges Row: WPS support, Band, Security
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (network.isWpsAvailable) {
                    SuggestionChip(
                        onClick = onCardClick,
                        label = { Text("WPS AVAILABLE", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        border = null
                    )
                }

                SuggestionChip(
                    onClick = onCardClick,
                    label = { Text(network.band, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    border = null
                )

                SuggestionChip(
                    onClick = onCardClick,
                    label = {
                        val secText = when {
                            network.capabilities.contains("WPA3", ignoreCase = true) -> "WPA3"
                            network.capabilities.contains("WPA2", ignoreCase = true) -> "WPA2"
                            network.capabilities.contains("WPA", ignoreCase = true) -> "WPA"
                            network.capabilities.contains("WEP", ignoreCase = true) -> "WEP"
                            else -> "Open"
                        }
                        Text(secText, fontSize = 10.sp)
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    border = null
                )
            }

            Spacer(Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onConnectPbc,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.RadioButtonChecked, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Push Button")
                }

                OutlinedButton(
                    onClick = onConnectPin,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("WPS PIN")
                }
            }
        }
    }
}
