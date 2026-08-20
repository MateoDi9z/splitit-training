package edu.austral.splitit.training.server.application.service

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import edu.austral.splitit.training.server.TEST_PASSWORD_HASH
import edu.austral.splitit.training.server.application.exception.EmailAlreadyInUseException
import edu.austral.splitit.training.server.application.port.PasswordHasher
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole
import edu.austral.splitit.training.server.domain.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignUpServiceTest {
    private val userRepository = mock<UserRepository>()
    private val passwordHasher = mock<PasswordHasher>()
    private val signUpService = SignUpService(userRepository, passwordHasher)

    @Test
    fun shouldCreateAssociateWithHashedPassword() {
        whenever(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false)
        whenever(passwordHasher.hash(TEST_PASSWORD)).thenReturn(TEST_PASSWORD_HASH)
        whenever(userRepository.save(any())).thenReturn(associate())

        val created = signUpService.signUp(TEST_EMAIL, TEST_PASSWORD)

        assertEquals(associate(), created)
        verify(userRepository).save(
            check { user ->
                assertEquals(null, user.id)
                assertEquals(TEST_EMAIL, user.email)
                assertEquals(TEST_PASSWORD_HASH, user.passwordHash)
                assertEquals(UserRole.ASSOCIATE, user.role)
            },
        )
    }

    @Test
    fun shouldNormalizeEmail() {
        whenever(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false)
        whenever(passwordHasher.hash(TEST_PASSWORD)).thenReturn(TEST_PASSWORD_HASH)
        whenever(userRepository.save(any())).thenReturn(associate())

        signUpService.signUp("  Socio@Mail.com  ", TEST_PASSWORD)

        verify(userRepository).existsByEmail(TEST_EMAIL)
        verify(userRepository).save(check { user -> assertEquals(TEST_EMAIL, user.email) })
    }

    @Test
    fun shouldFailIfEmailAlreadyInUse() {
        whenever(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true)

        val exception =
            assertFailsWith<EmailAlreadyInUseException> {
                signUpService.signUp(TEST_EMAIL, TEST_PASSWORD)
            }

        assertEquals(TEST_EMAIL, exception.email)
        verify(userRepository, never()).save(any<User>())
        verify(passwordHasher, never()).hash(any())
    }
}
