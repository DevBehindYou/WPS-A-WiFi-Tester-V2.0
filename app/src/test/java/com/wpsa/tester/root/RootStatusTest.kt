package com.wpsa.tester.root

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RootStatusTest {

    @Test
    fun testRootStateTransitions() {
        val unknown = RootState.UNKNOWN
        val checking = RootState.CHECKING
        val granted = RootState.GRANTED
        val denied = RootState.DENIED
        val unavailable = RootState.UNAVAILABLE

        assertEquals("UNKNOWN", unknown.name)
        assertEquals("CHECKING", checking.name)
        assertEquals("GRANTED", granted.name)
        assertEquals("DENIED", denied.name)
        assertEquals("UNAVAILABLE", unavailable.name)

        assertTrue(granted == RootState.GRANTED)
        assertFalse(denied == RootState.GRANTED)
    }
}
