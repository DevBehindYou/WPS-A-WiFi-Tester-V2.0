package sangiorgi.wps.opensource.wps

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.root.SuCommandExecutor

class PinValidationTest {

    private lateinit var pinManager: WpsPinManager

    @Before
    fun setup() {
        val executor = mock(SuCommandExecutor::class.java)
        val logger = AppLogger()
        pinManager = WpsPinManager(executor, logger)
    }

    @Test
    fun testValidChecksumEightDigitPins() {
        // WPS checksum algorithm: (3 * (d1 + d3 + d5 + d7) + 1 * (d2 + d4 + d6)) % 10
        // Checksum = (10 - accum % 10) % 10
        assertTrue(pinManager.isValidPin("12345670"))
        assertTrue(pinManager.isValidPin("00000000"))
        assertTrue(pinManager.isValidPin("83017568"))
    }

    @Test
    fun testValidFourDigitNumericPins() {
        assertTrue(pinManager.isValidPin("1234"))
        assertTrue(pinManager.isValidPin("0000"))
        assertTrue(pinManager.isValidPin("9876"))
    }

    @Test
    fun testInvalidChecksumEightDigitPins() {
        assertFalse(pinManager.isValidPin("12345671"))
        assertFalse(pinManager.isValidPin("83017563"))
        assertFalse(pinManager.isValidPin("12345678"))
    }

    @Test
    fun testInvalidLengthsAndNonNumeric() {
        assertFalse(pinManager.isValidPin(""))
        assertFalse(pinManager.isValidPin("123"))
        assertFalse(pinManager.isValidPin("12345"))
        assertFalse(pinManager.isValidPin("1234567"))
        assertFalse(pinManager.isValidPin("123456789"))
        assertFalse(pinManager.isValidPin("abcd"))
        assertFalse(pinManager.isValidPin("1234abcd"))
        assertFalse(pinManager.isValidPin("12 34 56"))
        assertFalse(pinManager.isValidPin("1234;reboot"))
    }
}
