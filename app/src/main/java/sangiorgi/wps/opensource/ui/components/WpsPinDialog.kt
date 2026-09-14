package sangiorgi.wps.opensource.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import sangiorgi.wps.opensource.wifi.WifiNetwork

@Composable
fun WpsPinDialog(
    network: WifiNetwork,
    onConnectPin: (String) -> Unit,
    onDismiss: () -> Unit,
    validatePin: (String) -> Boolean,
    validateChecksum: (String) -> Boolean
) {
    var pinText by remember { mutableStateOf("") }
    val isValid = remember(pinText) { validatePin(pinText) }
    val isEightDigits = pinText.length == 8

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        icon = {
            Icon(Icons.Default.Pin, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text(
                text = "Enter Authorized WPS PIN",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Target: ${network.ssid}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "BSSID: ${network.bssid}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = pinText,
                    onValueChange = { input ->
                        if (input.length <= 8 && input.all { it.isDigit() }) {
                            pinText = input
                        }
                    },
                    label = { Text("WPS PIN (4 or 8 digits)") },
                    placeholder = { Text("e.g. 12345670") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = if (isValid) ImeAction.Done else ImeAction.None
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isValid) onConnectPin(pinText)
                        }
                    ),
                    supportingText = {
                        when {
                            pinText.isEmpty() -> Text("Check your router label or management console.")
                            isValid && isEightDigits -> Text("✓ Valid 8-digit PIN with correct checksum", color = Color(0xFF4CAF50))
                            isValid -> Text("✓ Valid 4-digit PIN", color = Color(0xFF4CAF50))
                            isEightDigits -> Text("✗ Invalid 8-digit checksum", color = MaterialTheme.colorScheme.error)
                            else -> Text("PIN must be 4 or 8 digits (${pinText.length}/8)")
                        }
                    },
                    isError = pinText.isNotEmpty() && !isValid && isEightDigits
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConnectPin(pinText) },
                enabled = isValid
            ) {
                Text("Connect")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
