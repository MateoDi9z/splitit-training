package edu.austral.splitit.training.server.infrastructure.api.login

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import edu.austral.splitit.training.server.TEST_TOKEN
import edu.austral.splitit.training.server.application.exception.InvalidCredentialsException
import edu.austral.splitit.training.server.application.service.LoginResult
import edu.austral.splitit.training.server.application.service.LoginService
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoginControllerTest {
    private val loginService = mock<LoginService>()
    private val loginController = LoginController(loginService)

    @Test
    fun shouldReturnTokenAndUser() {
        whenever(loginService.login(TEST_EMAIL, TEST_PASSWORD))
            .thenReturn(LoginResult(token = TEST_TOKEN, user = associate()))

        val response = loginController.login(LoginRequest(TEST_EMAIL, TEST_PASSWORD))

        assertEquals(TEST_TOKEN, response.token)
        assertEquals(associate().id, response.user.id)
        assertEquals(TEST_EMAIL, response.user.email)
        assertEquals(UserRole.ASSOCIATE, response.user.role)
    }

    @Test
    fun shouldFailIfCredentialsAreInvalid() {
        whenever(loginService.login(TEST_EMAIL, TEST_PASSWORD))
            .thenThrow(InvalidCredentialsException())

        assertFailsWith<InvalidCredentialsException> {
            loginController.login(LoginRequest(TEST_EMAIL, TEST_PASSWORD))
        }
    }

    @Test
    fun shouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        val response = loginController.handleInvalidCredentials()

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals(mapOf("message" to "Invalid credentials"), response.body)
    }
}
