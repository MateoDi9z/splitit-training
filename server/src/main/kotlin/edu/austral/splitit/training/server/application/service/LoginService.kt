package edu.austral.splitit.training.server.application.service

import edu.austral.splitit.training.server.application.exception.InvalidCredentialsException
import edu.austral.splitit.training.server.application.port.PasswordHasher
import edu.austral.splitit.training.server.application.port.TokenProvider
import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.repository.UserRepository
import org.springframework.stereotype.Service

data class LoginResult(
    val token: String,
    val user: User,
)

@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenProvider: TokenProvider,
) {
    fun login(
        email: String,
        password: String,
    ): LoginResult {
        val normalizedEmail = email.trim().lowercase()
        val user =
            userRepository.findByEmail(normalizedEmail)
                ?: throw InvalidCredentialsException()
        if (!passwordHasher.matches(password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }
        return LoginResult(
            token = tokenProvider.issue(user),
            user = user,
        )
    }
}
