package edu.austral.splitit.training.server.domain.repository

import edu.austral.splitit.training.server.domain.model.User

interface UserRepository {
    fun save(user: User): User

    fun existsByEmail(email: String): Boolean

    fun findByEmail(email: String): User?
}
