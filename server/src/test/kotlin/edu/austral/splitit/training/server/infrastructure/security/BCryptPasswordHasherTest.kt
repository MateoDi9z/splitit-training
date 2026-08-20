package edu.austral.splitit.training.server.infrastructure.security

import edu.austral.splitit.training.server.TEST_PASSWORD
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BCryptPasswordHasherTest {
    private val hasher = BCryptPasswordHasher()

    @Test
    fun shouldHashWithoutKeepingPlaintext() {
        val hash = hasher.hash(TEST_PASSWORD)

        assertNotEquals(TEST_PASSWORD, hash)
        assertTrue(hash.startsWith("\$2a\$") || hash.startsWith("\$2b\$") || hash.startsWith("\$2y\$"))
    }

    @Test
    fun shouldMatchRawPasswordAgainstHash() {
        val hash = hasher.hash(TEST_PASSWORD)

        assertTrue(hasher.matches(TEST_PASSWORD, hash))
        assertFalse(hasher.matches("wrong-password", hash))
    }
}
