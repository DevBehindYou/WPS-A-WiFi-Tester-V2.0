package sangiorgi.wps.opensource.root

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootCapabilityDetector @Inject constructor(
    private val executor: SuCommandExecutor
) {
    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        Shell.isAppGrantedRoot() != false && Shell.getShell().isRoot
    }

    suspend fun getUid(): String {
        return executor.execute("id -u").outString.trim()
    }

    suspend fun getSELinuxStatus(): String {
        return executor.execute("getenforce").outString.trim()
    }

    suspend fun checkCommonCommands(): Map<String, Boolean> {
        val commands = listOf("wpa_cli", "wpa_supplicant", "iw", "ip", "getprop", "svc")
        val results = mutableMapOf<String, Boolean>()
        for (cmd in commands) {
            val res = executor.execute("which $cmd")
            results[cmd] = res.isSuccess && res.outString.isNotBlank()
        }
        return results
    }
}
