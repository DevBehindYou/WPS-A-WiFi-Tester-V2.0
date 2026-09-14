package com.wpsa.tester.wps

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.logging.LogLevel
import com.wpsa.tester.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

data class WpsCapabilities(
    val isPbcSupported: Boolean,
    val isPinSupported: Boolean,
    val discoveredSockets: List<String> = emptyList(),
    val wpaCliPath: String? = null
)

@Singleton
class WpaSupplicantDetector @Inject constructor(
    private val executor: SuCommandExecutor,
    private val logger: AppLogger
) {
    private val candidateSocketDirs = listOf(
        "/data/vendor/wifi/wpa/sockets",
        "/data/misc/wifi/sockets",
        "/data/system/wpa_supplicant",
        "/var/run/wpa_supplicant",
        "/dev/socket"
    )

    suspend fun detectWpsCapabilities(): WpsCapabilities = withContext(Dispatchers.IO) {
        val discoveredSockets = mutableListOf<String>()

        for (dir in candidateSocketDirs) {
            val check = executor.executeEscaped("test", "-d", dir)
            if (check.isSuccess) {
                discoveredSockets.add(dir)
                logger.log("WpaSupplicantDetector", "socket_check", "Discovered socket dir: $dir", level = LogLevel.DEBUG)
            }
        }

        // Find wpa_cli binary
        val whichCli = executor.execute("which wpa_cli")
        val wpaCliPath = if (whichCli.isSuccess && whichCli.stdout.isNotEmpty()) {
            whichCli.stdout.firstOrNull()?.trim()
        } else null

        // Check supported commands via wpa_cli help or string inspection
        var pbc = false
        var pin = false

        if (wpaCliPath != null) {
            val helpRes = executor.execute("wpa_cli help")
            if (helpRes.isSuccess) {
                val output = helpRes.outString
                pbc = output.contains("wps_pbc", ignoreCase = true)
                pin = output.contains("wps_pin", ignoreCase = true)
            } else {
                // Default to true if wpa_cli is present on Android (standard build includes WPS)
                pbc = true
                pin = true
            }
        }

        logger.log(
            component = "WpaSupplicantDetector",
            operation = "capabilities",
            result = "PBC: $pbc, PIN: $pin, sockets: ${discoveredSockets.size}, wpa_cli: $wpaCliPath",
            level = LogLevel.INFO
        )

        WpsCapabilities(
            isPbcSupported = pbc,
            isPinSupported = pin,
            discoveredSockets = discoveredSockets,
            wpaCliPath = wpaCliPath
        )
    }
}
