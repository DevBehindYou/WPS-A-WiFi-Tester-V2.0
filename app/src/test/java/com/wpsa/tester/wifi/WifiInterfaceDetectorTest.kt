package com.wpsa.tester.wifi

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.result.ShellResult
import com.wpsa.tester.root.SuCommandExecutor

class WifiInterfaceDetectorTest {

    private lateinit var executor: SuCommandExecutor
    private lateinit var logger: AppLogger
    private lateinit var detector: WifiInterfaceDetector

    @Before
    fun setup() {
        executor = mock(SuCommandExecutor::class.java)
        logger = AppLogger()
        detector = WifiInterfaceDetector(executor, logger)
    }

    @Test
    fun testDetectActiveInterfaceViaIwDev() = runBlocking {
        val iwOutput = listOf(
            "phy#0",
            "\tInterface wlan1",
            "\t\tifindex 28",
            "\t\twdev 0x1",
            "\t\taddr 12:34:56:78:9a:bc",
            "\t\ttype managed"
        )
        `when`(executor.execute("iw dev")).thenReturn(
            ShellResult("iw dev", true, 0, iwOutput, emptyList(), 10L)
        )

        val iface = detector.detectActiveInterface()
        assertNotNull(iface)
        assertEquals("wlan1", iface?.name)
        assertEquals("12:34:56:78:9a:bc", iface?.macAddress)
        assertEquals("UP", iface?.state)
    }

    @Test
    fun testIgnoresVirtualAndLoopbackInterfaces() = runBlocking {
        `when`(executor.execute("iw dev")).thenReturn(
            ShellResult("iw dev", false, 1, emptyList(), listOf("command not found"), 10L)
        )

        val ipLinkOutput = listOf(
            "1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT group default \\    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00",
            "2: dummy0: <BROADCAST,NOARP> mtu 1500 qdisc noop state DOWN mode DEFAULT group default \\    link/ether fa:ce:00:00:00:01 brd ff:ff:ff:ff:ff:ff",
            "3: tun0: <POINTOPOINT,MULTICAST,NOARP,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UNKNOWN \\    link/none",
            "28: wlan0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP mode DORMANT group default \\    link/ether 00:0a:f5:12:34:56 brd ff:ff:ff:ff:ff:ff"
        )
        `when`(executor.execute("ip -o link show")).thenReturn(
            ShellResult("ip -o link show", true, 0, ipLinkOutput, emptyList(), 10L)
        )

        val iface = detector.detectActiveInterface()
        assertNotNull(iface)
        assertEquals("wlan0", iface?.name)
        assertEquals("00:0a:f5:12:34:56", iface?.macAddress)
        assertEquals("UP", iface?.state)
    }

    @Test
    fun testFallbackWhenNoInterfacesFound() = runBlocking {
        `when`(executor.execute("iw dev")).thenReturn(
            ShellResult("iw dev", false, 1, emptyList(), emptyList(), 10L)
        )
        `when`(executor.execute("ip -o link show")).thenReturn(
            ShellResult("ip -o link show", false, 1, emptyList(), emptyList(), 10L)
        )
        `when`(executor.execute("ip link show wlan0")).thenReturn(
            ShellResult("ip link show wlan0", false, 1, emptyList(), emptyList(), 10L)
        )

        val iface = detector.detectActiveInterface()
        assertNull(iface)
    }
}
