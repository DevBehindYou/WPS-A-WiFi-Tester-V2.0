package com.wpsa.tester.wps

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.logging.LogLevel
import com.wpsa.tester.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WpsPbcManager @Inject constructor(
    private val executor: SuCommandExecutor,
    private val logger: AppLogger
) {
    private val bssidRegex = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    private val ifaceRegex = Regex("^[a-zA-Z0-9_.-]+$")

    suspend fun startWpsPbc(interfaceName: String, bssid: String? = null): Flow<WpsState> = flow {
        if (!ifaceRegex.matches(interfaceName)) {
            logger.log("WpsPbcManager", "startWpsPbc", "Invalid interface rejected: $interfaceName", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        if (!bssid.isNullOrEmpty() && !bssidRegex.matches(bssid)) {
            logger.log("WpsPbcManager", "startWpsPbc", "Invalid BSSID rejected: $bssid", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        emit(WpsState.PREPARING)
        logger.log("WpsPbcManager", "startWpsPbc", "Starting WPS PBC on $interfaceName target: ${bssid ?: "any"}", level = LogLevel.INFO)

        val res = if (bssid.isNullOrEmpty()) {
            executor.executeEscaped("wpa_cli", "-i", interfaceName, "wps_pbc")
        } else {
            executor.executeEscaped("wpa_cli", "-i", interfaceName, "wps_pbc", bssid)
        }

        if (!res.isSuccess || res.outString.contains("FAIL")) {
            logger.log("WpsPbcManager", "startWpsPbc", "wps_pbc command failed: ${res.outString}", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        emit(WpsState.WAITING_FOR_ROUTER)

        var timeoutCounter = 0
        val maxTimeout = 120 // 120 seconds standard WPS PBC window

        while (timeoutCounter < maxTimeout) {
            delay(1000)
            timeoutCounter++

            val statusRes = executor.executeEscaped("wpa_cli", "-i", interfaceName, "status")
            if (statusRes.isSuccess) {
                val statusStr = statusRes.outString
                if (statusStr.contains("wpa_state=COMPLETED")) {
                    emit(WpsState.OBTAINING_IP)
                    delay(2000)
                    emit(WpsState.CONNECTED)
                    return@flow
                } else if (statusStr.contains("wpa_state=ASSOCIATING")) {
                    emit(WpsState.ASSOCIATING)
                } else if (statusStr.contains("wpa_state=AUTHENTICATING")) {
                    emit(WpsState.AUTHENTICATING)
                }
            }
        }

        emit(WpsState.TIMED_OUT)
        executor.executeEscaped("wpa_cli", "-i", interfaceName, "wps_cancel")
    }

    suspend fun cancelWps(interfaceName: String) = withContext(Dispatchers.IO) {
        if (ifaceRegex.matches(interfaceName)) {
            logger.log("WpsPbcManager", "cancelWps", "Cancelling active WPS session on $interfaceName", level = LogLevel.INFO)
            executor.executeEscaped("wpa_cli", "-i", interfaceName, "wps_cancel")
        }
    }
}
