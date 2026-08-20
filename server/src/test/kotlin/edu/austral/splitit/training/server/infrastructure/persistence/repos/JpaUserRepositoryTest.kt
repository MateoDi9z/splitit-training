package edu.austral.splitit.training.server.infrastructure.persistence.repos

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD_HASH
import edu.austral.splitit.training.server.TEST_USER_ID
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import edu.austral.splitit.training.server.infrastructure.persistence.entities.UserEntity
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JpaUserRepositoryTest {
    private val userJpaRepository = mock<UserJpaRepository>()
    private val jpaUserRepository = JpaUserRepository(userJpaRepository)

    @Test
    fun shouldSaveMappingToEntityAndBack() {
        whenever(userJpaRepository.save(any())).thenReturn(
            UserEntity(
                id = TEST_USER_ID,
                email = TEST_EMAIL,
                passwordHash = TEST_PASSWORD_HASH,
                role = UserRole.ASSOCIATE,
            ),
        )

        val saved = jpaUserRepository.save(associate(id = null))

        assertEquals(associate(), saved)
        verify(userJpaRepository).save(
            check { entity ->
                assertEquals(null, entity.id)
                assertEquals(TEST_EMAIL, entity.email)
                assertEquals(TEST_PASSWORD_HASH, entity.passwordHash)
                assertEquals(UserRole.ASSOCIATE, entity.role)
            },
        )
    }

    @Test
    fun shouldReturnTrueWhenEmailExists() {
        whenever(userJpaRepository.existsByEmail(TEST_EMAIL)).thenReturn(true)

        assertTrue(jpaUserRepository.existsByEmail(TEST_EMAIL))
    }

    @Test
    fun shouldReturnFalseWhenEmailDoesNotExist() {
        whenever(userJpaRepository.existsByEmail(TEST_EMAIL)).thenReturn(false)

        assertFalse(jpaUserRepository.existsByEmail(TEST_EMAIL))
    }

    @Test
    fun shouldFindByEmail() {
        whenever(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(
            UserEntity(
                id = TEST_USER_ID,
                email = TEST_EMAIL,
                passwordHash = TEST_PASSWORD_HASH,
                role = UserRole.ASSOCIATE,
            ),
        )

        assertEquals(associate(), jpaUserRepository.findByEmail(TEST_EMAIL))
    }

    @Test
    fun shouldReturnNullWhenEmailNotFound() {
        whenever(userJpaRepository.findByEmail(TEST_EMAIL)).thenReturn(null)

        assertNull(jpaUserRepository.findByEmail(TEST_EMAIL))
    }
}
