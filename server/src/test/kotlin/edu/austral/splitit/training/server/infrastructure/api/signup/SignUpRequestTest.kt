package edu.austral.splitit.training.server.infrastructure.api.signup

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD
import jakarta.validation.Validation
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SignUpRequestTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun shouldAcceptValidRequest() {
        val violations = validator.validate(SignUpRequest(TEST_EMAIL, TEST_PASSWORD))

        assertTrue(violations.isEmpty())
    }

    @Test
    fun shouldFailIfEmailIsBlank() {
        val violations = validator.validate(SignUpRequest(" ", TEST_PASSWORD))

        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun shouldFailIfEmailIsInvalid() {
        val violations = validator.validate(SignUpRequest("not-an-email", TEST_PASSWORD))

        assertTrue(violations.any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun shouldFailIfPasswordIsTooShort() {
        val violations = validator.validate(SignUpRequest(TEST_EMAIL, "short"))

        assertTrue(violations.any { it.propertyPath.toString() == "password" })
        assertEquals(MIN_PASSWORD_LENGTH, 8)
    }
}
