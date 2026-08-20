package edu.austral.splitit.training.server.infrastructure.api.signup

import edu.austral.splitit.training.server.application.exception.EmailAlreadyInUseException
import edu.austral.splitit.training.server.application.service.SignUpService
import edu.austral.splitit.training.server.infrastructure.api.UserResponse
import edu.austral.splitit.training.server.infrastructure.api.toUserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpController(
    private val signUpService: SignUpService,
) {
    @PostMapping("/api/auth/signup")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ResponseEntity<UserResponse> {
        val user = signUpService.signUp(request.email, request.password)
        return ResponseEntity.status(HttpStatus.CREATED).body(user.toUserResponse())
    }

    @ExceptionHandler(EmailAlreadyInUseException::class)
    fun handleEmailAlreadyInUse(): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("message" to "Email already in use"))
}
