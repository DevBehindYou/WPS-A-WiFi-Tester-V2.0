package com.wpsa.tester.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
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

            // Section 4: About WPS/A Tester
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
                        text = "Authorized Wi-Fi Testing Utility:\nDesigned for testing and administrative maintenance of Wi-Fi routers owned by or explicitly authorized for the user. Operates 100% locally with zero cloud telemetry or tracking. Does not implement password cracking, default PIN databases, or unauthorized exploitation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
