package edu.austral.splitit.training.server.infrastructure.api.login

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import jakarta.validation.Validation
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LoginRequestTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun shouldAcceptValidRequest() {
        val violations = validator.validate(LoginRequest(TEST_EMAIL, TEST_PASSWORD))

        assertTrue(violations.isEmpty())
    }

    @Test
    fun shouldFailIfEmailIsInvalid() {
        val violations = validator.validate(LoginRequest("not-an-email", TEST_PASSWORD))

        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun shouldFailIfPasswordIsBlank() {
        val violations = validator.validate(LoginRequest(TEST_EMAIL, " "))

        assertTrue(violations.any { it.propertyPath.toString() == "password" })
    }
}
