package edu.austral.splitit.training.server.infrastructure.security

import edu.austral.splitit.training.server.application.port.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordHasher : PasswordHasher {
    private val encoder = BCryptPasswordEncoder()

    override fun hash(raw: String): String = requireNotNull(encoder.encode(raw))

    override fun matches(
        raw: String,
        hash: String,
    ): Boolean = encoder.matches(raw, hash) == true
}
