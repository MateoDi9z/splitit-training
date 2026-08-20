package edu.austral.splitit.training.server.infrastructure.api.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

const val EXAMPLE_PONG = "pong"

@RestController
public class PingController {
    @GetMapping("/api/ping")
    fun ping(): String = EXAMPLE_PONG
}
