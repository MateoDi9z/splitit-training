package edu.austral.splitit.training.server.application.service

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import edu.austral.splitit.training.server.TEST_PASSWORD_HASH
import edu.austral.splitit.training.server.TEST_TOKEN
import edu.austral.splitit.training.server.application.exception.InvalidCredentialsException
import edu.austral.splitit.training.server.application.port.PasswordHasher
import edu.austral.splitit.training.server.application.port.TokenProvider
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.repository.UserRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoginServiceTest {
    private val userRepository = mock<UserRepository>()
    private val passwordHasher = mock<PasswordHasher>()
    private val tokenProvider = mock<TokenProvider>()
    private val loginService = LoginService(userRepository, passwordHasher, tokenProvider)

    @Test
    fun shouldReturnTokenWhenCredentialsMatch() {
        val user = associate()
        whenever(userRepository.findByEmail(TEST_EMAIL)).thenReturn(user)
        whenever(passwordHasher.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(true)
        whenever(tokenProvider.issue(user)).thenReturn(TEST_TOKEN)

        val result = loginService.login(TEST_EMAIL, TEST_PASSWORD)

        assertEquals(TEST_TOKEN, result.token)
        assertEquals(user, result.user)
    }

    @Test
    fun shouldNormalizeEmail() {
        val user = associate()
        whenever(userRepository.findByEmail(TEST_EMAIL)).thenReturn(user)
        whenever(passwordHasher.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(true)
        whenever(tokenProvider.issue(user)).thenReturn(TEST_TOKEN)

        loginService.login("  Socio@Mail.com  ", TEST_PASSWORD)

        verify(userRepository).findByEmail(TEST_EMAIL)
    }

    @Test
    fun shouldFailIfUserDoesNotExist() {
        whenever(userRepository.findByEmail(TEST_EMAIL)).thenReturn(null)

        assertFailsWith<InvalidCredentialsException> {
            loginService.login(TEST_EMAIL, TEST_PASSWORD)
        }

        verify(passwordHasher, never()).matches(any(), any())
        verify(tokenProvider, never()).issue(any<User>())
    }

    @Test
    fun shouldFailIfPasswordDoesNotMatch() {
        whenever(userRepository.findByEmail(TEST_EMAIL)).thenReturn(associate())
        whenever(passwordHasher.matches(TEST_PASSWORD, TEST_PASSWORD_HASH)).thenReturn(false)

        assertFailsWith<InvalidCredentialsException> {
            loginService.login(TEST_EMAIL, TEST_PASSWORD)
        }

        verify(tokenProvider, never()).issue(any<User>())
    }
}
