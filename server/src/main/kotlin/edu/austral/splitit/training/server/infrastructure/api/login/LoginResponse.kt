package edu.austral.splitit.training.server.infrastructure.api.login

import edu.austral.splitit.training.server.infrastructure.api.UserResponse

data class LoginResponse(
    val token: String,
    val user: UserResponse,
)
