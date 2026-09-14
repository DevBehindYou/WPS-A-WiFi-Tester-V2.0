package com.wpsa.tester.wps

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.result.ShellResult
import com.wpsa.tester.root.SuCommandExecutor

class WpaSupplicantDetectorTest {

    private lateinit var executor: SuCommandExecutor
    private lateinit var logger: AppLogger
    private lateinit var detector: WpaSupplicantDetector

    @Before
    fun setup() {
        executor = mock(SuCommandExecutor::class.java)
        logger = AppLogger()
        detector = WpaSupplicantDetector(executor, logger)
    }

    @Test
    fun testDetectCapabilitiesWithDiscoveredSocketAndWpaCli() = runBlocking {
        // Socket check
        `when`(executor.executeEscaped("test", "-d", "/data/vendor/wifi/wpa/sockets")).thenReturn(
            ShellResult("test -d /data/vendor/wifi/wpa/sockets", true, 0, emptyList(), emptyList(), 5L)
        )
        `when`(executor.executeEscaped("test", "-d", "/data/misc/wifi/sockets")).thenReturn(
            ShellResult("test -d /data/misc/wifi/sockets", false, 1, emptyList(), emptyList(), 5L)
        )
        `when`(executor.executeEscaped("test", "-d", "/data/system/wpa_supplicant")).thenReturn(
            ShellResult("test -d /data/system/wpa_supplicant", false, 1, emptyList(), emptyList(), 5L)
        )
        `when`(executor.executeEscaped("test", "-d", "/var/run/wpa_supplicant")).thenReturn(
            ShellResult("test -d /var/run/wpa_supplicant", false, 1, emptyList(), emptyList(), 5L)
        )
        `when`(executor.executeEscaped("test", "-d", "/dev/socket")).thenReturn(
            ShellResult("test -d /dev/socket", false, 1, emptyList(), emptyList(), 5L)
        )

        // which wpa_cli
        `when`(executor.execute("which wpa_cli")).thenReturn(
            ShellResult("which wpa_cli", true, 0, listOf("/system/bin/wpa_cli"), emptyList(), 5L)
        )

        // wpa_cli help
        val helpOutput = listOf(
            "commands:",
            "  wps_pbc [bssid] = start Wi-Fi Protected Setup Push Button Configuration",
            "  wps_pin <bssid> [pin] = start WPS PIN"
        )
        `when`(executor.execute("wpa_cli help")).thenReturn(
            ShellResult("wpa_cli help", true, 0, helpOutput, emptyList(), 5L)
        )

        val caps = detector.detectWpsCapabilities()
        assertNotNull(caps)
        assertEquals("/system/bin/wpa_cli", caps.wpaCliPath)
        assertTrue(caps.isPbcSupported)
        assertTrue(caps.isPinSupported)
        assertTrue(caps.discoveredSockets.contains("/data/vendor/wifi/wpa/sockets"))
    }

    @Test
    fun testDetectCapabilitiesWhenWpaCliMissing() = runBlocking {
        // Socket check all fail
        val dirs = listOf(
            "/data/vendor/wifi/wpa/sockets",
            "/data/misc/wifi/sockets",
            "/data/system/wpa_supplicant",
            "/var/run/wpa_supplicant",
            "/dev/socket"
        )
        dirs.forEach { dir ->
            `when`(executor.executeEscaped("test", "-d", dir)).thenReturn(
                ShellResult("test -d $dir", false, 1, emptyList(), emptyList(), 5L)
            )
        }

        `when`(executor.execute("which wpa_cli")).thenReturn(
            ShellResult("which wpa_cli", false, 1, emptyList(), listOf("not found"), 5L)
        )

        val caps = detector.detectWpsCapabilities()
        assertNull(caps.wpaCliPath)
        assertFalse(caps.isPbcSupported)
        assertFalse(caps.isPinSupported)
        assertTrue(caps.discoveredSockets.isEmpty())
    }
}
