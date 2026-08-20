package edu.austral.splitit.training.server.application.service

import edu.austral.splitit.training.server.application.exception.EmailAlreadyInUseException
import edu.austral.splitit.training.server.application.port.PasswordHasher
import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole
import edu.austral.splitit.training.server.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
) {
    fun signUp(
        email: String,
        password: String,
    ): User {
        val normalizedEmail = email.trim().lowercase()
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw EmailAlreadyInUseException(normalizedEmail)
        }
        val user =
            User(
                email = normalizedEmail,
                passwordHash = passwordHasher.hash(password),
                role = UserRole.ASSOCIATE,
            )
        return userRepository.save(user)
    }
}
