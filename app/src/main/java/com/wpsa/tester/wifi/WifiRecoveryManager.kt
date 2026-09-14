package com.wpsa.tester.wifi

import android.content.Context
import android.net.wifi.WifiManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.wpsa.tester.root.SuCommandExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiRecoveryManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val executor: SuCommandExecutor
) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var wasWifiEnabled: Boolean = true
    private var initialNetworkId: Int = -1

    suspend fun snapshotState() = withContext(Dispatchers.IO) {
        wasWifiEnabled = wifiManager.isWifiEnabled
        initialNetworkId = wifiManager.connectionInfo?.networkId ?: -1
    }

    suspend fun restoreState(interfaceName: String) = withContext(Dispatchers.IO) {
        // Try restoring using wpa_cli first
        executor.execute("wpa_cli -i $interfaceName reconnect")
        
        // Ensure WiFi is enabled if it was before
        if (wasWifiEnabled && !wifiManager.isWifiEnabled) {
            @Suppress("DEPRECATION")
            wifiManager.isWifiEnabled = true
        }

        // We avoid destructive recovery if possible. Reconnecting normally handles it.
    }
}
