package sangiorgi.wps.opensource.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sangiorgi.wps.opensource.wps.WpsState

@Composable
fun WpsDialog(
    state: WpsState,
    networkName: String,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val isTerminal = state in listOf(
        WpsState.CONNECTED,
        WpsState.FAILED,
        WpsState.TIMED_OUT,
        WpsState.CANCELLED,
        WpsState.UNSUPPORTED
    )

    AlertDialog(
        onDismissRequest = {
            if (isTerminal) onDismiss()
        },
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Wifi, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "WPS Connection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Target Network: $networkName",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                AnimatedContent(targetState = state, label = "WpsStateAnim") { targetState ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (targetState) {
                            WpsState.IDLE, WpsState.PREPARING -> {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Text("Preparing supplicant and Wi-Fi interface...", style = MaterialTheme.typography.bodyMedium)
                            }
                            WpsState.WAITING_FOR_ROUTER -> {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Text(
                                    "Waiting for router WPS session...\nPress the WPS button on your Wi-Fi router now.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                            WpsState.AUTHENTICATING -> {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Text("Authenticating with access point...", style = MaterialTheme.typography.bodyMedium)
                            }
                            WpsState.ASSOCIATING -> {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Text("Associating and exchanging credentials...", style = MaterialTheme.typography.bodyMedium)
                            }
                            WpsState.OBTAINING_IP -> {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                Text("WPS handshake complete! Requesting DHCP IP...", style = MaterialTheme.typography.bodyMedium)
                            }
                            WpsState.CONNECTED -> {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(56.dp))
                                Text(
                                    "Connected Successfully!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text("Your device is now securely connected to $networkName.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                            WpsState.FAILED -> {
                                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(56.dp))
                                Text("WPS Handshake Failed", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                Text("The access point rejected the session or supplicant encountered an error.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                            WpsState.TIMED_OUT -> {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(56.dp))
                                Text("WPS Session Timed Out", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                Text("No WPS PBC button press detected within the timeout window.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                            WpsState.CANCELLED -> {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(56.dp))
                                Text("Session Cancelled", style = MaterialTheme.typography.titleMedium)
                            }
                            WpsState.UNSUPPORTED -> {
                                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(56.dp))
                                Text("WPS Not Supported", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                                Text("The target router does not support or has locked WPS.", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isTerminal) {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!isTerminal) {
                OutlinedButton(onClick = onCancel) {
                    Text("Cancel WPS")
                }
            }
        }
    )
}
