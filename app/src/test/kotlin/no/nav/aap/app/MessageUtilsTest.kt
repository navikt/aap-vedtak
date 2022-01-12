package no.nav.aap.app

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

internal class MessageUtilsTest {
    @Test
    fun testGetMessage() {
        assertEquals("Hello      World!", "Hello      World!")
    }
}
