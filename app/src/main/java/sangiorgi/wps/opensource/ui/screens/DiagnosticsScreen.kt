package sangiorgi.wps.opensource.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import sangiorgi.wps.opensource.diagnostics.DeviceDiagnostics
import sangiorgi.wps.opensource.diagnostics.DeviceDiagnosticsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    diagnostics: DeviceDiagnostics
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var data by remember { mutableStateOf<DeviceDiagnosticsData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    fun loadDiagnostics() {
        coroutineScope.launch {
            isLoading = true
            data = diagnostics.gatherDiagnostics()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadDiagnostics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Diagnostics", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(
                        onClick = {
                            data?.let {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Diagnostics Report", it.toMarkdown())
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Full diagnostics report copied", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = data != null
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy all diagnostics")
                    }
                    IconButton(onClick = { loadDiagnostics() }, enabled = !isLoading) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh diagnostics")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading && data == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val state = data
            if (state == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Failed to gather diagnostics. Tap refresh to retry.")
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExpandableCard(
                        title = "📱 Device & Android OS",
                        subtitle = "${state.manufacturer} ${state.model} (Android ${state.osVersion})",
                        defaultExpanded = true,
                        items = listOf(
                            "Manufacturer" to state.manufacturer,
                            "Model" to state.model,
                            "Brand / Board" to "${state.brand} / ${state.board}",
                            "Android Version" to "${state.osVersion} (API ${state.apiLevel})",
                            "Security Patch" to state.securityPatch
                        )
                    )

                    ExpandableCard(
                        title = "🛡️ Root & SELinux",
                        subtitle = if (state.isRootGranted) "Granted (${state.rootVersion ?: "Magisk"})" else "Denied / Unavailable",
                        defaultExpanded = true,
                        items = listOf(
                            "Root Granted" to (if (state.isRootGranted) "Yes (libsu active)" else "No"),
                            "Root Provider / Version" to (state.rootVersion ?: "Standard / Auto"),
                            "SELinux Mode" to state.seLinuxMode
                        )
                    )

                    ExpandableCard(
                        title = "📶 Wi-Fi Subsystem",
                        subtitle = "Interface: ${state.activeInterface ?: "Not detected"}",
                        defaultExpanded = true,
                        items = listOf(
                            "Active Interface" to (state.activeInterface ?: "Not detected"),
                            "MAC Address" to (state.macAddress ?: "Unknown"),
                            "Wi-Fi Hardware Active" to (if (state.isWifiEnabled) "Enabled" else "Disabled"),
                            "Location Services" to (if (state.isLocationEnabled) "Active (Ready for scan)" else "Disabled (Required)")
                        )
                    )

                    ExpandableCard(
                        title = "⚙️ Supplicant & Sockets",
                        subtitle = "wpa_cli: ${if (state.wpaCliPath != null) "Available" else "Missing"}",
                        defaultExpanded = true,
                        items = listOf(
                            "wpa_cli Binary" to (state.wpaCliPath ?: "Not found"),
                            "WPS PBC Supported" to if (state.pbcSupported) "Yes" else "No",
                            "WPS PIN Supported" to if (state.pinSupported) "Yes" else "No",
                            "Discovered Sockets" to if (state.discoveredSockets.isEmpty()) "Standard / Default" else state.discoveredSockets.joinToString("\n")
                        )
                    )

                    ExpandableCard(
                        title = "🛠️ System Shell Commands",
                        subtitle = "${state.availableCommands.values.count { it }}/${state.availableCommands.size} available",
                        defaultExpanded = false,
                        items = state.availableCommands.map { it.key to if (it.value) "Available" else "Not found" }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    subtitle: String,
    defaultExpanded: Boolean = true,
    items: List<Pair<String, String>>
) {
    var expanded by remember { mutableStateOf(defaultExpanded) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                items.forEach { (label, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.45f)
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(0.55f)
                        )
                    }
                }
            }
        }
    }
}
