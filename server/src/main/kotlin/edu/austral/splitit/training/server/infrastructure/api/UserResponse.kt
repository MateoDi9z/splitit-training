package edu.austral.splitit.training.server.infrastructure.api

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole

data class UserResponse(
    val id: Long,
    val email: String,
    val role: UserRole,
)

fun User.toUserResponse(): UserResponse =
    UserResponse(
        id = requireNotNull(id) { "Persisted user must have an id" },
        email = email,
        role = role,
    )
