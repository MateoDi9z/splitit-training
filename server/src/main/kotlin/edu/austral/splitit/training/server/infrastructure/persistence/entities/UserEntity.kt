package edu.austral.splitit.training.server.infrastructure.persistence.entities

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(name = "password_hash", nullable = false)
    val passwordHash: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,
)

fun UserEntity.toDomain(): User =
    User(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = role,
    )

fun User.toEntity(): UserEntity =
    UserEntity(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = role,
    )
