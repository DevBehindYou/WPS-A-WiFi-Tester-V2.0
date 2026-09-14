package com.wpsa.tester.wps

import org.junit.Assert.assertEquals
import org.junit.Test
import com.wpsa.tester.wifi.WifiNetwork

class WpsNetworkTest {

    @Test
    fun testWpsCapabilityDetection() {
        val networkWithWps = WifiNetwork(
            ssid = "TestNetwork",
            bssid = "00:11:22:33:44:55",
            capabilities = "[WPA2-PSK-CCMP][WPS]",
            rssi = -50,
            frequency = 2412,
            isWpsAvailable = true
        )

        assertEquals(true, networkWithWps.isWpsAvailable)
        assertEquals("00:11:22:33:44:55", networkWithWps.bssid)
        assertEquals("TestNetwork", networkWithWps.ssid)
        assertEquals("2.4 GHz", networkWithWps.band)
        assertEquals("Excellent", networkWithWps.signalStrength)
    }
}
