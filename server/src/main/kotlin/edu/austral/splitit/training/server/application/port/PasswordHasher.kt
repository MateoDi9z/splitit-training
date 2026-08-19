package edu.austral.splitit.training.server.application.port

interface PasswordHasher {
    fun hash(raw: String): String
}
