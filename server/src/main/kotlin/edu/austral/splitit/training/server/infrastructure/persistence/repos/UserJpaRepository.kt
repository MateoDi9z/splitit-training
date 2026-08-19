package edu.austral.splitit.training.server.infrastructure.persistence.repos

import edu.austral.splitit.training.server.infrastructure.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmail(email: String): Boolean
}
