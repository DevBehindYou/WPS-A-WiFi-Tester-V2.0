package sangiorgi.wps.opensource.wps

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WpsEventParserTest {

    @Test
    fun testParseSuccessAndCompletion() {
        assertEquals(WpsState.CONNECTED, WpsEventParser.parseEvent("<3>CTRL-EVENT-WPS-SUCCESS"))
        assertEquals(WpsState.CONNECTED, WpsEventParser.parseEvent("wpa_state=COMPLETED"))
    }

    @Test
    fun testParseIntermediateStates() {
        assertEquals(WpsState.AUTHENTICATING, WpsEventParser.parseEvent("wpa_state=AUTHENTICATING"))
        assertEquals(WpsState.ASSOCIATING, WpsEventParser.parseEvent("wpa_state=ASSOCIATING"))
        assertEquals(WpsState.WAITING_FOR_ROUTER, WpsEventParser.parseEvent("wpa_state=SCANNING"))
        assertEquals(WpsState.WAITING_FOR_ROUTER, WpsEventParser.parseEvent("wpa_state=DISCONNECTED"))
        assertEquals(WpsState.OBTAINING_IP, WpsEventParser.parseEvent("wpa_state=OBTAINING_IP"))
    }

    @Test
    fun testParseFailureAndTimeout() {
        assertEquals(WpsState.TIMED_OUT, WpsEventParser.parseEvent("<3>CTRL-EVENT-WPS-TIMEOUT"))
        assertEquals(WpsState.FAILED, WpsEventParser.parseEvent("<3>CTRL-EVENT-WPS-FAIL msg=1 config_error=0"))
        assertEquals(WpsState.FAILED, WpsEventParser.parseEvent("<3>WPS-OVERLAP-DETECTED"))
        assertEquals(WpsState.FAILED, WpsEventParser.parseEvent("FAIL"))
    }

    @Test
    fun testUnrecognizedEventsReturnNullWithoutCrashing() {
        assertNull(WpsEventParser.parseEvent("CTRL-EVENT-REGDOM-CHANGE init=BEACON type=UNKNOWN"))
        assertNull(WpsEventParser.parseEvent(""))
        assertNull(WpsEventParser.parseEvent("RANDOM_LOG_LINE_12345"))
    }
}
