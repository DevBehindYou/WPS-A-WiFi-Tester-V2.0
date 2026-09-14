package com.wpsa.tester.wps

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.logging.LogLevel
import com.wpsa.tester.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WpsPinManager @Inject constructor(
    private val executor: SuCommandExecutor,
    private val logger: AppLogger
) {
    private val bssidRegex = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    private val ifaceRegex = Regex("^[a-zA-Z0-9_.-]+$")

    /**
     * Connects using a validated WPS PIN to a target BSSID on the given interface.
     */
    suspend fun startWpsPin(interfaceName: String, bssid: String, pin: String): Flow<WpsState> = flow {
        if (!ifaceRegex.matches(interfaceName)) {
            logger.log("WpsPinManager", "startWpsPin", "Invalid interface rejected: $interfaceName", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        if (!bssidRegex.matches(bssid)) {
            logger.log("WpsPinManager", "startWpsPin", "Invalid BSSID rejected: $bssid", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        if (!isValidPin(pin)) {
            logger.log("WpsPinManager", "startWpsPin", "Invalid PIN format or checksum rejected", level = LogLevel.WARN)
            emit(WpsState.FAILED)
            return@flow
        }

        emit(WpsState.PREPARING)
        logger.log("WpsPinManager", "startWpsPin", "Starting WPS PIN for $bssid on $interfaceName", level = LogLevel.INFO)

        val res = executor.executeEscaped("wpa_cli", "-i", interfaceName, "wps_pin", bssid, pin.trim())
        if (!res.isSuccess || res.outString.contains("FAIL")) {
            logger.log("WpsPinManager", "startWpsPin", "wps_pin command failed: ${res.outString}", level = LogLevel.ERROR)
            emit(WpsState.FAILED)
            return@flow
        }

        emit(WpsState.AUTHENTICATING)

        var timeoutCounter = 0
        val maxTimeout = 60 // 60s timeout for PIN negotiation

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

    /**
     * Validates a WPS PIN:
     * - 8 digits: standard WPS checksum verification
     * - 4 digits: numeric validation
     */
    fun isValidPin(pin: String): Boolean {
        val trimmed = pin.trim()
        if (!trimmed.all { it.isDigit() }) return false

        return when (trimmed.length) {
            8 -> validateChecksum(trimmed)
            4 -> true
            else -> false
        }
    }

    fun validateChecksum(pin: String): Boolean {
        if (pin.length != 8 || !pin.all { it.isDigit() }) return false

        var accum = 0
        for (i in 0 until 7) {
            var digit = pin[i].digitToInt()
            if (i % 2 == 0) {
                digit *= 3
            }
            accum += digit
        }

        val checksum = (10 - (accum % 10)) % 10
        return checksum == pin[7].digitToInt()
    }
}
