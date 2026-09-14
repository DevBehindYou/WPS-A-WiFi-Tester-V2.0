package com.wpsa.tester.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticsTest {

    @Test
    fun testDeviceDiagnosticsToMarkdown() {
        val data = DeviceDiagnosticsData(
            manufacturer = "Xiaomi",
            model = "23076RN4BI",
            brand = "Redmi",
            board = "sky",
            osVersion = "14",
            apiLevel = 34,
            securityPatch = "2024-05-01",
            isRootGranted = true,
            rootVersion = "27.0:MAGISKSU",
            seLinuxMode = "Enforcing",
            activeInterface = "wlan1",
            macAddress = "02:00:00:00:00:00",
            isWifiEnabled = true,
            isLocationEnabled = true,
            pbcSupported = true,
            pinSupported = true,
            discoveredSockets = listOf("/data/vendor/wifi/wpa/sockets"),
            wpaCliPath = "/system/bin/wpa_cli",
            availableCommands = mapOf("iw" to true, "ip" to true, "wpa_cli" to true)
        )

        val md = data.toMarkdown()

        assertTrue(md.contains("# WPS/A Tester — Device Diagnostics Report"))
        assertTrue(md.contains("- **Manufacturer:** Xiaomi"))
        assertTrue(md.contains("- **Model:** 23076RN4BI"))
        assertTrue(md.contains("- **Android:** 14 (API 34)"))
        assertTrue(md.contains("- **Root Granted:** true"))
        assertTrue(md.contains("- **Root Version:** 27.0:MAGISKSU"))
        assertTrue(md.contains("- **SELinux Mode:** Enforcing"))
        assertTrue(md.contains("- **Active Interface:** wlan1"))
        assertTrue(md.contains("- **wpa_cli Path:** /system/bin/wpa_cli"))
        assertTrue(md.contains("- **iw:** Available"))
    }

    @Test
    fun testDiagnosticsFallbackValues() {
        val data = DeviceDiagnosticsData(
            manufacturer = "Unknown",
            model = "Generic",
            brand = "Generic",
            board = "unknown",
            osVersion = "13",
            apiLevel = 33,
            securityPatch = "Unknown",
            isRootGranted = false,
            rootVersion = null,
            seLinuxMode = "Permissive",
            activeInterface = null,
            macAddress = null,
            isWifiEnabled = false,
            isLocationEnabled = false,
            pbcSupported = false,
            pinSupported = false,
            discoveredSockets = emptyList(),
            wpaCliPath = null,
            availableCommands = emptyMap()
        )

        val md = data.toMarkdown()

        assertTrue(md.contains("- **Root Version:** N/A"))
        assertTrue(md.contains("- **Active Interface:** Not Detected"))
        assertTrue(md.contains("- **Discovered Sockets:** None"))
        assertTrue(md.contains("- **wpa_cli Path:** Not found"))
    }
}
