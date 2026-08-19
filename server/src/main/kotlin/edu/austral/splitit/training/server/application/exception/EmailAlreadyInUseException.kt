package edu.austral.splitit.training.server.application.exception

class EmailAlreadyInUseException(
    val email: String,
) : RuntimeException("Email already in use: $email")
