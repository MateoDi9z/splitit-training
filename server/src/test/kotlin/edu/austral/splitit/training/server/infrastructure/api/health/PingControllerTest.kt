package edu.austral.splitit.training.server.infrastructure.api.health

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PingControllerTest {
    @Test
    fun shouldReturnPong() {
        assertEquals(EXAMPLE_PONG, PingController().ping())
    }
}
