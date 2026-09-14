package com.wpsa.tester.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wpsa.tester.data.SettingsRepository
import com.wpsa.tester.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository
) {
    val settings by settingsRepository.settings.collectAsState()
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "100% Offline & Zero Telemetry",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "WPS/A Tester operates completely offline. It does not contain any analytics SDKs, crash trackers, ad libraries, or third-party telemetry endpoints. No network packets are ever transmitted to remote servers.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Volatile In-Memory Processing",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "All scanned Wi-Fi information (SSIDs, BSSIDs, frequencies, signal levels) and diagnostic logs are maintained strictly in volatile device memory during the active session. Nothing is stored persistently or synced externally.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Device Permissions & Root",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "• Location & Nearby Wi-Fi: Required by Android to scan wireless access points. No location coordinates are recorded or tracked.\n• Superuser / Root: Used exclusively to interact with local wpa_supplicant control interfaces and diagnostic binaries (iw, ip, getenforce).",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "No Credential or Data Harvesting",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "The application does not collect, record, export, or transmit Wi-Fi passwords, personal credentials, hardware IDs, or private device data.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            icon = { Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "STRICT WARNING: Authorized Testing Only",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "WPS/A Tester is intended solely and strictly for testing Wi-Fi network security, evaluating router WPS configuration weaknesses, and conducting authorized administrative diagnostics.\n\nYou are legally permitted to use this application ONLY on wireless networks and equipment that you personally own or have received explicit, documented, and verifiable written consent to audit from the rightful owner.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Prohibited Conduct & Criminal Liability",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Unauthorized scanning, connection attempts, packet injection, or attacks against third-party wireless infrastructure without prior authorization are strictly prohibited and constitute criminal violations of applicable computer crime laws, including the United States Computer Fraud and Abuse Act (CFAA, 18 U.S.C. § 1030), the UK Computer Misuse Act 1990, and international cybercrime statutes.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Sole Purpose: Wi-Fi Security & Diagnostics",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "This application only tests Wi-Fi security and is built for testing and educational purposes. It does NOT implement automated PIN brute-forcing, dictionary attacks, Pixie Dust exploitation, or credential harvesting.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "Disclaimer of Warranty & Liability",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "This software is provided 'AS IS' under the GNU Affero General Public License v3.0 (AGPL-3.0) without warranties of any kind, express or implied. The developers, contributors, and original authors disclaim any and all liability for damages, data loss, network disruption, or legal consequences resulting from use or misuse of this tool.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("I Understand & Agree")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimensions.screenPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Appearance
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Application Theme", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                            onClick = { settingsRepository.updateThemeMode("SYSTEM") },
                            selected = settings.themeMode == "SYSTEM",
                            icon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        ) {
                            Text("System")
                        }

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                            onClick = { settingsRepository.updateThemeMode("DARK") },
                            selected = settings.themeMode == "DARK",
                            icon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        ) {
                            Text("Dark")
                        }

                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                            onClick = { settingsRepository.updateThemeMode("LIGHT") },
                            selected = settings.themeMode == "LIGHT",
                            icon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        ) {
                            Text("Light")
                        }
                    }
                }
            }

            // Section 2: General & Session
            Text(
                text = "WPS Session & Hardware",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ListItem(
                        headlineContent = { Text("Keep screen awake during WPS") },
                        supportingContent = { Text("Prevents Android sleep during 120-second WPS countdown") },
                        trailingContent = {
                            Switch(
                                checked = settings.keepScreenAwake,
                                onCheckedChange = { settingsRepository.updateKeepScreenAwake(it) }
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    ListItem(
                        headlineContent = { Text("Auto-detect Wi-Fi Interface") },
                        supportingContent = { Text("Automatically queries active driver interface (e.g. wlan0, wlan1)") },
                        trailingContent = {
                            Switch(
                                checked = settings.autoDetectInterface,
                                onCheckedChange = { settingsRepository.updateAutoDetectInterface(it) }
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )

                    if (!settings.autoDetectInterface) {
                        OutlinedTextField(
                            value = settings.manualInterface,
                            onValueChange = { settingsRepository.updateManualInterface(it) },
                            label = { Text("Manual Interface Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "WPS Handshake Timeout: ${settings.wpsTimeoutSec}s",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = settings.wpsTimeoutSec.toFloat(),
                        onValueChange = { settingsRepository.updateWpsTimeout(it.toInt()) },
                        valueRange = 30f..180f,
                        steps = 4
                    )
                }
            }

            // Section 3: Logging
            Text(
                text = "Logging",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ListItem(
                        headlineContent = { Text("Verbose Diagnostics Logging") },
                        supportingContent = { Text("Logs shell exit codes, supplicant events, and interface transitions") },
                        trailingContent = {
                            Switch(
                                checked = settings.enableVerboseLogging,
                                onCheckedChange = { settingsRepository.updateVerboseLogging(it) }
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )
                }
            }

            // Section 4: Legal & Compliance
            Text(
                text = "Legal & Compliance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    ListItem(
                        headlineContent = { Text("Privacy Policy", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("100% offline, zero telemetry, volatile local memory only") },
                        leadingContent = {
                            Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable { showPrivacyPolicyDialog = true },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )

                    HorizontalDivider()

                    ListItem(
                        headlineContent = { Text("Terms & Conditions", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Authorized testing scope, strict legal notice & AGPL-3.0") },
                        leadingContent = {
                            Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        },
                        modifier = Modifier.clickable { showTermsDialog = true },
                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                    )
                }
            }

            // Section 5: About WPS/A Tester
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                shape = RoundedCornerShape(Dimensions.cardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("WPS/A Tester", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Version 1.0.0 • Architecture Release", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Application ID: com.wpsa.tester",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Authorized Wi-Fi Testing Utility:\nDesigned solely for testing and administrative maintenance of Wi-Fi routers owned by or explicitly authorized for the user. Operates 100% locally with zero cloud telemetry or tracking. Does not implement password cracking, default PIN databases, or unauthorized exploitation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
