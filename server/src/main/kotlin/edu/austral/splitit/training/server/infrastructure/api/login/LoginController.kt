package edu.austral.splitit.training.server.infrastructure.api.login

import edu.austral.splitit.training.server.application.exception.InvalidCredentialsException
import edu.austral.splitit.training.server.application.service.LoginService
import edu.austral.splitit.training.server.infrastructure.api.toUserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(
    private val loginService: LoginService,
) {
    @PostMapping("/api/auth/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): LoginResponse {
        val result = loginService.login(request.email, request.password)
        return LoginResponse(
            token = result.token,
            user = result.user.toUserResponse(),
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Invalid credentials"))
}
