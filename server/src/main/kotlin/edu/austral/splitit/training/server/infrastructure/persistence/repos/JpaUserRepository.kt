package edu.austral.splitit.training.server.infrastructure.persistence.repos

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.repository.UserRepository
import edu.austral.splitit.training.server.infrastructure.persistence.entities.toDomain
import edu.austral.splitit.training.server.infrastructure.persistence.entities.toEntity
import org.springframework.stereotype.Repository

@Repository
class JpaUserRepository(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User = userJpaRepository.save(user.toEntity()).toDomain()

    override fun existsByEmail(email: String): Boolean = userJpaRepository.existsByEmail(email)
}
