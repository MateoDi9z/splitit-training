package edu.austral.splitit.training.server.infrastructure.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
public class PingController {
    @GetMapping("/api/ping")
    fun ping(): String { return "pong" }
}