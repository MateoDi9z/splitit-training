package edu.austral.splitit.training.server.application.port

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole

data class AuthenticatedUser(
    val id: Long,
    val email: String,
    val role: UserRole,
)

interface TokenProvider {
    fun issue(user: User): String

    fun parse(token: String): AuthenticatedUser?
}
