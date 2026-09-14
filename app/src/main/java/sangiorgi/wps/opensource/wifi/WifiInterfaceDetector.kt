package sangiorgi.wps.opensource.wifi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.core.logging.LogLevel
import sangiorgi.wps.opensource.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

data class WifiInterface(
    val name: String,
    val macAddress: String? = null,
    val state: String = "UNKNOWN"
)

@Singleton
class WifiInterfaceDetector @Inject constructor(
    private val executor: SuCommandExecutor,
    private val logger: AppLogger
) {
    private val ignoredPrefixes = listOf("lo", "dummy", "tun", "tap", "rmnet", "sit", "ip6tnl", "p2p", "bond", "ifb", "v4-")

    suspend fun detectActiveInterface(): WifiInterface? = withContext(Dispatchers.IO) {
        // Method 1: iw dev (direct Wi-Fi subsystem query - most accurate)
        val iwRes = executor.execute("iw dev")
        if (iwRes.isSuccess) {
            val lines = iwRes.stdout
            var currentName = ""
            var currentMac: String? = null
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("Interface")) {
                    currentName = trimmed.substringAfter("Interface").trim()
                } else if (trimmed.startsWith("addr") && currentName.isNotEmpty()) {
                    currentMac = trimmed.substringAfter("addr").trim()
                    if (ignoredPrefixes.none { currentName.startsWith(it) }) {
                        logger.log(
                            component = "WifiInterfaceDetector",
                            operation = "detect",
                            result = "Found interface $currentName (MAC: $currentMac) via iw dev",
                            level = LogLevel.INFO
                        )
                        return@withContext WifiInterface(name = currentName, macAddress = currentMac, state = "UP")
                    }
                }
            }
            if (currentName.isNotEmpty() && ignoredPrefixes.none { currentName.startsWith(it) }) {
                return@withContext WifiInterface(name = currentName, state = "UP")
            }
        }

        // Method 2: ip link (prioritize wlan/wifi interfaces)
        val ipLinkRes = executor.execute("ip -o link show")
        if (ipLinkRes.isSuccess) {
            val lines = ipLinkRes.stdout
            var fallbackIface: WifiInterface? = null
            for (line in lines) {
                val regex = Regex("""^\d+:\s+([a-zA-Z0-9_.-]+):""")
                val match = regex.find(line)
                if (match != null) {
                    val ifaceName = match.groupValues[1]
                    if (ignoredPrefixes.none { ifaceName.startsWith(it) }) {
                        val isUp = line.contains("UP")
                        val macMatch = Regex("""link/ether\s+([0-9a-fA-F:]{17})""").find(line)
                        val mac = macMatch?.groupValues?.get(1)
                        val ifaceObj = WifiInterface(name = ifaceName, macAddress = mac, state = if (isUp) "UP" else "DOWN")

                        // If it starts with wlan or wifi, it's definitely the primary Wi-Fi interface!
                        if (ifaceName.startsWith("wlan") || ifaceName.startsWith("wifi")) {
                            logger.log(
                                component = "WifiInterfaceDetector",
                                operation = "detect",
                                result = "Found primary interface $ifaceName (UP: $isUp) via ip link",
                                level = LogLevel.INFO
                            )
                            return@withContext ifaceObj
                        } else if (fallbackIface == null) {
                            fallbackIface = ifaceObj
                        }
                    }
                }
            }
            if (fallbackIface != null) {
                return@withContext fallbackIface
            }
        }

        // Default fallback to standard wlan0 if present
        val testWlan0 = executor.execute("ip link show wlan0")
        if (testWlan0.isSuccess) {
            logger.log("WifiInterfaceDetector", "detect", "Fallback to default wlan0", level = LogLevel.WARN)
            return@withContext WifiInterface(name = "wlan0", state = "UP")
        }

        logger.log("WifiInterfaceDetector", "detect", "No Wi-Fi interface detected", level = LogLevel.ERROR)
        null
    }
}
