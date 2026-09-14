package sangiorgi.wps.opensource.wps

object WpsEventParser {

    /**
     * Parses a wpa_supplicant event or status line into a corresponding [WpsState].
     * Preserves robustness by returning null on unrecognized messages without throwing.
     */
    fun parseEvent(event: String): WpsState? {
        val trimmed = event.trim()
        return when {
            trimmed.contains("WPS-SUCCESS", ignoreCase = true) ||
                    trimmed.contains("wpa_state=COMPLETED", ignoreCase = true) -> WpsState.CONNECTED

            trimmed.contains("wpa_state=OBTAINING_IP", ignoreCase = true) -> WpsState.OBTAINING_IP

            trimmed.contains("wpa_state=ASSOCIATING", ignoreCase = true) -> WpsState.ASSOCIATING

            trimmed.contains("wpa_state=AUTHENTICATING", ignoreCase = true) -> WpsState.AUTHENTICATING

            trimmed.contains("WPS-TIMEOUT", ignoreCase = true) -> WpsState.TIMED_OUT

            trimmed.contains("WPS-FAIL", ignoreCase = true) ||
                    trimmed.contains("WPS-OVERLAP-DETECTED", ignoreCase = true) ||
                    trimmed.contains("FAIL", ignoreCase = false) -> WpsState.FAILED

            trimmed.contains("wpa_state=SCANNING", ignoreCase = true) ||
                    trimmed.contains("wpa_state=DISCONNECTED", ignoreCase = true) -> WpsState.WAITING_FOR_ROUTER

            else -> null
        }
    }
}
