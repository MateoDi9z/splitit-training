package edu.austral.splitit.training.server.domain.model

data class User(
    val id: Long? = null,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
)
