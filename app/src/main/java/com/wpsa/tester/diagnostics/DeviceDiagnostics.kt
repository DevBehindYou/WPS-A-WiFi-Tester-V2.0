package com.wpsa.tester.diagnostics

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.wpsa.tester.root.RootCapabilityDetector
import com.wpsa.tester.root.SuCommandExecutor
import com.wpsa.tester.wifi.WifiInterfaceDetector
import com.wpsa.tester.wifi.WifiScanner
import com.wpsa.tester.wps.CommandAvailabilityDetector
import com.wpsa.tester.wps.WpaSupplicantDetector
import javax.inject.Inject
import javax.inject.Singleton

data class DeviceDiagnosticsData(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val board: String,
    val osVersion: String,
    val apiLevel: Int,
    val securityPatch: String,
    val isRootGranted: Boolean,
    val rootVersion: String?,
    val seLinuxMode: String,
    val activeInterface: String?,
    val macAddress: String?,
    val isWifiEnabled: Boolean,
    val isLocationEnabled: Boolean,
    val pbcSupported: Boolean,
    val pinSupported: Boolean,
    val discoveredSockets: List<String>,
    val wpaCliPath: String?,
    val availableCommands: Map<String, Boolean>
) {
    fun toMarkdown(): String {
        return buildString {
            appendLine("# WPS/A Tester — Device Diagnostics Report")
            appendLine()
            appendLine("## Device Information")
            appendLine("- **Manufacturer:** $manufacturer")
            appendLine("- **Model:** $model")
            appendLine("- **Brand / Board:** $brand / $board")
            appendLine("- **Android:** $osVersion (API $apiLevel)")
            appendLine("- **Security Patch:** $securityPatch")
            appendLine()
            appendLine("## Root & Security")
            appendLine("- **Root Granted:** $isRootGranted")
            appendLine("- **Root Version:** ${rootVersion ?: "N/A"}")
            appendLine("- **SELinux Mode:** $seLinuxMode")
            appendLine()
            appendLine("## Wi-Fi Subsystem")
            appendLine("- **Active Interface:** ${activeInterface ?: "Not Detected"}")
            appendLine("- **MAC Address:** ${macAddress ?: "Unknown"}")
            appendLine("- **Wi-Fi Hardware Enabled:** $isWifiEnabled")
            appendLine("- **Location Services Enabled:** $isLocationEnabled")
            appendLine()
            appendLine("## Supplicant & WPS")
            appendLine("- **wpa_cli Path:** ${wpaCliPath ?: "Not found"}")
            appendLine("- **WPS PBC Supported:** $pbcSupported")
            appendLine("- **WPS PIN Supported:** $pinSupported")
            appendLine("- **Discovered Sockets:** ${if (discoveredSockets.isEmpty()) "None" else discoveredSockets.joinToString(", ")}")
            appendLine()
            appendLine("## Available System Commands")
            availableCommands.forEach { (cmd, avail) ->
                appendLine("- **$cmd:** ${if (avail) "Available" else "Not Found"}")
            }
        }
    }
}

@Singleton
class DeviceDiagnostics @Inject constructor(
    private val rootCapabilityDetector: RootCapabilityDetector,
    private val wifiInterfaceDetector: WifiInterfaceDetector,
    private val wpaSupplicantDetector: WpaSupplicantDetector,
    private val commandAvailabilityDetector: CommandAvailabilityDetector,
    private val wifiScanner: WifiScanner,
    private val executor: SuCommandExecutor
) {
    suspend fun gatherDiagnostics(): DeviceDiagnosticsData = withContext(Dispatchers.IO) {
        val rootAvail = rootCapabilityDetector.isRootAvailable()
        val selinux = if (rootAvail) rootCapabilityDetector.getSELinuxStatus() else "Unknown"

        var rootVer: String? = null
        if (rootAvail) {
            val suVerRes = executor.execute("su -v")
            if (suVerRes.isSuccess && suVerRes.stdout.isNotEmpty()) {
                rootVer = suVerRes.stdout.firstOrNull()?.trim()
            }
        }

        val ifaceObj = if (rootAvail) wifiInterfaceDetector.detectActiveInterface() else null
        val wpsCaps = if (rootAvail) wpaSupplicantDetector.detectWpsCapabilities() else null
        val commands = if (rootAvail) commandAvailabilityDetector.checkAvailableCommands() else emptyMap()

        DeviceDiagnosticsData(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            brand = Build.BRAND,
            board = Build.BOARD,
            osVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH,
            isRootGranted = rootAvail,
            rootVersion = rootVer,
            seLinuxMode = selinux,
            activeInterface = ifaceObj?.name,
            macAddress = ifaceObj?.macAddress,
            isWifiEnabled = wifiScanner.isWifiEnabled(),
            isLocationEnabled = wifiScanner.isLocationEnabled(),
            pbcSupported = wpsCaps?.isPbcSupported ?: false,
            pinSupported = wpsCaps?.isPinSupported ?: false,
            discoveredSockets = wpsCaps?.discoveredSockets ?: emptyList(),
            wpaCliPath = wpsCaps?.wpaCliPath,
            availableCommands = commands
        )
    }
}
