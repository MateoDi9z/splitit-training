package edu.austral.splitit.training.server.infrastructure.api.signup

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import edu.austral.splitit.training.server.application.exception.EmailAlreadyInUseException
import edu.austral.splitit.training.server.application.service.SignUpService
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class SignUpControllerTest {
    private val signUpService = mock<SignUpService>()
    private val signUpController = SignUpController(signUpService)

    @Test
    fun shouldReturnCreatedUser() {
        whenever(signUpService.signUp(TEST_EMAIL, TEST_PASSWORD)).thenReturn(associate())

        val response = signUpController.signUp(SignUpRequest(TEST_EMAIL, TEST_PASSWORD))

        assertEquals(HttpStatus.CREATED, response.statusCode)
        val body = assertNotNull(response.body)
        assertEquals(associate().id, body.id)
        assertEquals(TEST_EMAIL, body.email)
        assertEquals(UserRole.ASSOCIATE, body.role)
    }

    @Test
    fun shouldFailIfEmailAlreadyInUse() {
        whenever(signUpService.signUp(TEST_EMAIL, TEST_PASSWORD))
            .thenThrow(EmailAlreadyInUseException(TEST_EMAIL))

        assertFailsWith<EmailAlreadyInUseException> {
            signUpController.signUp(SignUpRequest(TEST_EMAIL, TEST_PASSWORD))
        }
    }

    @Test
    fun shouldReturnConflictWhenEmailAlreadyInUse() {
        val response = signUpController.handleEmailAlreadyInUse()

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(mapOf("message" to "Email already in use"), response.body)
    }
}
