package com.wpsa.tester.wps

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StandardSupplicantAdapter @Inject constructor(
    private val pbcManager: WpsPbcManager,
    private val pinManager: WpsPinManager
) : SupplicantAdapter {

    override suspend fun startPbc(interfaceName: String, bssid: String?): Flow<WpsState> {
        return pbcManager.startWpsPbc(interfaceName, bssid)
    }

    override suspend fun startPin(interfaceName: String, bssid: String, pin: String): Flow<WpsState> {
        return pinManager.startWpsPin(interfaceName, bssid, pin)
    }

    override suspend fun cancel(interfaceName: String) {
        pbcManager.cancelWps(interfaceName)
    }
}
