package edu.austral.splitit.training.server.infrastructure.security

import edu.austral.splitit.training.server.application.port.AuthenticatedUser
import edu.austral.splitit.training.server.application.port.TokenProvider
import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.crypto.SecretKey

const val MIN_JWT_SECRET_LENGTH = 32

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.expiration-hours}") private val expirationHours: Long,
) : TokenProvider {
    private val key: SecretKey

    init {
        require(secret.length >= MIN_JWT_SECRET_LENGTH) {
            "JWT_SECRET must be at least $MIN_JWT_SECRET_LENGTH characters"
        }
        key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    override fun issue(user: User): String {
        val now = Instant.now()
        val id = requireNotNull(user.id) { "Cannot issue token for user without id" }
        return Jwts
            .builder()
            .subject(id.toString())
            .claim(EMAIL_CLAIM, user.email)
            .claim(ROLE_CLAIM, user.role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()
    }

    override fun parse(token: String): AuthenticatedUser? =
        try {
            val claims =
                Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .payload
            val email = claims[EMAIL_CLAIM] as? String ?: return null
            val roleName = claims[ROLE_CLAIM] as? String ?: return null
            val role = runCatching { UserRole.valueOf(roleName) }.getOrNull() ?: return null
            AuthenticatedUser(
                id = claims.subject.toLong(),
                email = email,
                role = role,
            )
        } catch (_: JwtException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }

    companion object {
        private const val EMAIL_CLAIM = "email"
        private const val ROLE_CLAIM = "role"
    }
}
