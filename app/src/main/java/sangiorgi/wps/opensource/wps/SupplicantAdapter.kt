package sangiorgi.wps.opensource.wps

import kotlinx.coroutines.flow.Flow

interface SupplicantAdapter {
    /**
     * Attempts to start PBC via the best method for the current device/OEM.
     */
    suspend fun startPbc(interfaceName: String, bssid: String? = null): Flow<WpsState>
    
    /**
     * Attempts to start PIN via the best method for the current device/OEM.
     */
    suspend fun startPin(interfaceName: String, bssid: String, pin: String): Flow<WpsState>
    
    /**
     * Cancels any ongoing WPS operation.
     */
    suspend fun cancel(interfaceName: String)
}
