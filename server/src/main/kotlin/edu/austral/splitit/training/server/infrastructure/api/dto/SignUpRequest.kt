package edu.austral.splitit.training.server.infrastructure.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

const val MIN_PASSWORD_LENGTH = 8

data class SignUpRequest(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    @field:Size(min = MIN_PASSWORD_LENGTH)
    val password: String,
)
