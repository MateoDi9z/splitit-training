package edu.austral.splitit.training.server.infrastructure.security

import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import edu.austral.splitit.training.server.librarian
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private const val JWT_SECRET = "01234567890123456789012345678901"
private const val OTHER_JWT_SECRET = "abcdefghijklmnopqrstuvwxyzabcdef"
private const val EXPIRATION_HOURS = 8L

class JwtTokenProviderTest {
    private val tokenProvider = JwtTokenProvider(JWT_SECRET, EXPIRATION_HOURS)

    @Test
    fun shouldRoundTripAssociateClaims() {
        val token = tokenProvider.issue(associate())
        val parsed = assertNotNull(tokenProvider.parse(token))

        assertEquals(associate().id, parsed.id)
        assertEquals(associate().email, parsed.email)
        assertEquals(UserRole.ASSOCIATE, parsed.role)
    }

    @Test
    fun shouldRoundTripLibrarianRole() {
        val parsed = assertNotNull(tokenProvider.parse(tokenProvider.issue(librarian())))

        assertEquals(UserRole.LIBRARIAN, parsed.role)
        assertEquals(librarian().email, parsed.email)
    }

    @Test
    fun shouldFailIfUserHasNoId() {
        assertFailsWith<IllegalArgumentException> {
            tokenProvider.issue(associate(id = null))
        }
    }

    @Test
    fun shouldFailIfSecretIsTooShort() {
        assertFailsWith<IllegalArgumentException> {
            JwtTokenProvider("too-short", EXPIRATION_HOURS)
        }
    }

    @Test
    fun shouldReturnNullForGarbageToken() {
        assertNull(tokenProvider.parse("not-a-jwt"))
    }

    @Test
    fun shouldReturnNullForTokenSignedWithAnotherSecret() {
        val token = JwtTokenProvider(OTHER_JWT_SECRET, EXPIRATION_HOURS).issue(associate())

        assertNull(tokenProvider.parse(token))
    }

    @Test
    fun shouldReturnNullForExpiredToken() {
        val expiredProvider = JwtTokenProvider(JWT_SECRET, -1)
        val token = expiredProvider.issue(associate())

        assertNull(tokenProvider.parse(token))
    }
}
