package sangiorgi.wps.opensource.wps

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.root.SuCommandExecutor

class WpsPinManagerTest {

    private lateinit var pinManager: WpsPinManager
    private lateinit var executor: SuCommandExecutor
    private lateinit var logger: AppLogger

    @Before
    fun setup() {
        executor = mock(SuCommandExecutor::class.java)
        logger = AppLogger()
        pinManager = WpsPinManager(executor, logger)
    }

    @Test
    fun testValidPinChecksum() {
        // Known valid 8-digit WPS PINs
        assertTrue(pinManager.isValidPin("12345670"))
        assertTrue(pinManager.isValidPin("00000000"))
        assertTrue(pinManager.isValidPin("83017568"))

        // Valid 4-digit numeric PIN
        assertTrue(pinManager.isValidPin("1234"))
        assertTrue(pinManager.isValidPin("0000"))
    }

    @Test
    fun testInvalidPinChecksum() {
        // Invalid lengths or characters
        assertFalse(pinManager.isValidPin("1234567"))
        assertFalse(pinManager.isValidPin("123456789"))
        assertFalse(pinManager.isValidPin("1234567A"))
        assertFalse(pinManager.isValidPin("12 3456"))
        assertFalse(pinManager.isValidPin("1234;reboot"))

        // Invalid checksums (8-digit)
        assertFalse(pinManager.isValidPin("12345671"))
        assertFalse(pinManager.isValidPin("83017563"))
    }
}
