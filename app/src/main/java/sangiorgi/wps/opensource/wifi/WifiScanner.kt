package sangiorgi.wps.opensource.wifi

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import androidx.core.location.LocationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.core.logging.LogLevel
import sangiorgi.wps.opensource.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

data class WifiNetwork(
    val ssid: String,
    val bssid: String,
    val capabilities: String,
    val rssi: Int,
    val frequency: Int,
    val isWpsAvailable: Boolean
) {
    val band: String get() = when {
        frequency in 2400..2500 -> "2.4 GHz"
        frequency in 4900..5900 -> "5 GHz"
        frequency > 5900 -> "6 GHz"
        else -> "Unknown Band"
    }

    val signalStrength: String get() = when {
        rssi >= -50 -> "Excellent"
        rssi >= -65 -> "Good"
        rssi >= -80 -> "Fair"
        else -> "Weak"
    }
}

@Singleton
class WifiScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val executor: SuCommandExecutor,
    private val logger: AppLogger
) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun isWifiEnabled(): Boolean {
        return try {
            wifiManager.isWifiEnabled
        } catch (e: Exception) {
            false
        }
    }

    fun isLocationEnabled(): Boolean {
        return try {
            LocationManagerCompat.isLocationEnabled(locationManager)
        } catch (e: Exception) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getNearbyNetworks(): List<WifiNetwork> = withContext(Dispatchers.IO) {
        _isScanning.value = true
        try {
            logger.log("WifiScanner", "scan", "Querying framework scanResults...", level = LogLevel.DEBUG)
            val frameworkResults = try {
                wifiManager.scanResults.mapNotNull { result ->
                    val bssid = result.BSSID ?: return@mapNotNull null
                    val ssid = result.SSID?.takeIf { it.isNotBlank() } ?: "<Hidden SSID>"
                    val caps = result.capabilities ?: ""
                    WifiNetwork(
                        ssid = ssid,
                        bssid = bssid,
                        capabilities = caps,
                        rssi = result.level,
                        frequency = result.frequency,
                        isWpsAvailable = caps.contains("WPS", ignoreCase = true)
                    )
                }
            } catch (e: Exception) {
                logger.log("WifiScanner", "scan", "Framework scanResults failed: ${e.message}", level = LogLevel.WARN)
                emptyList()
            }

            if (frameworkResults.isNotEmpty()) {
                logger.log("WifiScanner", "scan", "Framework returned ${frameworkResults.size} networks", level = LogLevel.INFO)
                return@withContext frameworkResults.sortedByDescending { it.rssi }
            }

            // Root fallback 1: cmd wifi list-scan-results
            logger.log("WifiScanner", "scan", "Attempting root fallback via 'cmd wifi list-scan-results'", level = LogLevel.DEBUG)
            val cmdResults = parseCmdWifiResults()
            if (cmdResults.isNotEmpty()) {
                logger.log("WifiScanner", "scan", "Root cmd wifi returned ${cmdResults.size} networks", level = LogLevel.INFO)
                return@withContext cmdResults.sortedByDescending { it.rssi }
            }

            // Root fallback 2: wpa_cli scan_results
            logger.log("WifiScanner", "scan", "Attempting root fallback via 'wpa_cli scan_results'", level = LogLevel.DEBUG)
            val wpaResults = parseWpaCliResults()
            logger.log("WifiScanner", "scan", "Root wpa_cli returned ${wpaResults.size} networks", level = LogLevel.INFO)
            wpaResults.sortedByDescending { it.rssi }
        } finally {
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(): Boolean {
        return try {
            wifiManager.startScan()
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun parseCmdWifiResults(): List<WifiNetwork> {
        val res = executor.execute("cmd wifi list-scan-results")
        if (!res.isSuccess) return emptyList()

        val list = mutableListOf<WifiNetwork>()
        // Format: BSSID Frequency RSSI Age(sec) SSID Flags
        val bssidRegex = Regex("""([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}""")

        for (line in res.stdout) {
            val trimmed = line.trim()
            if (trimmed.startsWith("BSSID") || trimmed.isEmpty()) continue
            val match = bssidRegex.find(trimmed) ?: continue
            val bssid = match.value
            val afterBssid = trimmed.substring(match.range.last + 1).trim()
            val tokens = afterBssid.split(Regex("""\s+"""))
            if (tokens.size >= 3) {
                val freq = tokens[0].toIntOrNull() ?: 2412
                val rssiToken = tokens[1].substringBefore("(").toIntOrNull() ?: -70
                // Flags are in brackets like [WPA-PSK-CCMP][WPS]
                val flagsMatch = Regex("""(\[[^\]]+\])+""").find(afterBssid)
                val flags = flagsMatch?.value ?: ""
                val ssid = if (flagsMatch != null) {
                    val idx = afterBssid.indexOf(flagsMatch.value)
                    val candidate = afterBssid.substring(0, idx).trim()
                    // Remove frequency and RSSI and Age tokens
                    val remainingTokens = candidate.split(Regex("""\s+"""))
                    if (remainingTokens.size >= 3) {
                        remainingTokens.drop(3).joinToString(" ").ifBlank { "<Hidden SSID>" }
                    } else candidate.ifBlank { "<Hidden SSID>" }
                } else "<Unknown SSID>"

                list.add(
                    WifiNetwork(
                        ssid = ssid,
                        bssid = bssid,
                        capabilities = flags,
                        rssi = rssiToken,
                        frequency = freq,
                        isWpsAvailable = flags.contains("WPS", ignoreCase = true)
                    )
                )
            }
        }
        return list
    }

    private suspend fun parseWpaCliResults(): List<WifiNetwork> {
        val res = executor.execute("wpa_cli scan_results")
        if (!res.isSuccess) return emptyList()

        val list = mutableListOf<WifiNetwork>()
        // Format: bssid / frequency / signal level / flags / ssid
        for (line in res.stdout) {
            val parts = line.split("\t")
            if (parts.size >= 4) {
                val bssid = parts[0].trim()
                if (!bssid.contains(":")) continue
                val freq = parts[1].trim().toIntOrNull() ?: 2412
                val rssi = parts[2].trim().toIntOrNull() ?: -70
                val flags = parts[3].trim()
                val ssid = if (parts.size >= 5) parts[4].trim().ifBlank { "<Hidden SSID>" } else "<Hidden SSID>"

                list.add(
                    WifiNetwork(
                        ssid = ssid,
                        bssid = bssid,
                        capabilities = flags,
                        rssi = rssi,
                        frequency = freq,
                        isWpsAvailable = flags.contains("WPS", ignoreCase = true)
                    )
                )
            }
        }
        return list
    }
}
