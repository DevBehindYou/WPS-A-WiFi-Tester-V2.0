package com.wpsa.tester.root

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.result.ShellResult

class SuCommandExecutorTest {

    private lateinit var logger: AppLogger
    private lateinit var executor: SuCommandExecutor

    @Before
    fun setup() {
        logger = AppLogger()
        executor = SuCommandExecutor(logger)
    }

    @Test
    fun testShellResultProperties() {
        val result = ShellResult(
            command = "id",
            isSuccess = true,
            exitCode = 0,
            stdout = listOf("uid=0(root) gid=0(root)"),
            stderr = emptyList(),
            durationMs = 45L
        )

        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertEquals(0, result.exitCode)
        assertEquals(45L, result.durationMs)
        assertEquals("uid=0(root) gid=0(root)", result.outString)
        assertEquals("", result.errString)
    }

    @Test
    fun testDangerousCommandArgumentRejection() = runBlocking {
        // Test semicolons, pipes, backticks, and newlines
        val dangerousInputs = listOf(
            "wlan0; rm -rf /",
            "wlan0 | cat /etc/passwd",
            "wlan0`reboot`",
            "wlan0\nwhoami",
            "wlan0 > /sdcard/leak.txt"
        )

        for (badInput in dangerousInputs) {
            val res = executor.executeEscaped("wpa_cli", "-i", badInput, "status")
            assertFalse("Argument with dangerous characters should be rejected: $badInput", res.isSuccess)
            assertEquals(-1, res.exitCode)
            assertTrue(res.stderr.any { it.contains("Security violation") })
        }
    }
}
