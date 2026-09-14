package sangiorgi.wps.opensource.wps

import sangiorgi.wps.opensource.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommandAvailabilityDetector @Inject constructor(
    private val executor: SuCommandExecutor
) {
    suspend fun checkAvailableCommands(): Map<String, Boolean> {
        val commands = listOf("wpa_cli", "wpa_supplicant", "iw", "ip", "getprop", "svc")
        val results = mutableMapOf<String, Boolean>()
        
        for (cmd in commands) {
            val res = executor.execute("which $cmd")
            results[cmd] = res.isSuccess && res.outString.isNotBlank()
        }
        
        return results
    }
}
