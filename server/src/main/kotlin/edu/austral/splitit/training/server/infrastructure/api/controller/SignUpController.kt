package edu.austral.splitit.training.server.infrastructure.api.controller

import edu.austral.splitit.training.server.application.exception.EmailAlreadyInUseException
import edu.austral.splitit.training.server.application.service.SignUpService
import edu.austral.splitit.training.server.infrastructure.api.dto.SignUpRequest
import edu.austral.splitit.training.server.infrastructure.api.dto.SignUpResponse
import edu.austral.splitit.training.server.infrastructure.api.dto.toSignUpResponse
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
    ): ResponseEntity<SignUpResponse> {
        val user = signUpService.signUp(request.email, request.password)
        return ResponseEntity.status(HttpStatus.CREATED).body(user.toSignUpResponse())
    }

    @ExceptionHandler(EmailAlreadyInUseException::class)
    fun handleEmailAlreadyInUse(): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(mapOf("message" to "Email already in use"))
}
