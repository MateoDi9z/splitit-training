package edu.austral.splitit.training.server.infrastructure.api.dto

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole

data class SignUpResponse(
    val id: Long,
    val email: String,
    val role: UserRole,
)

fun User.toSignUpResponse(): SignUpResponse =
    SignUpResponse(
        id = requireNotNull(id) { "Persisted user must have an id" },
        email = email,
        role = role,
    )
