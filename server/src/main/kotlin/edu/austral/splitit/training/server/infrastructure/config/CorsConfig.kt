package edu.austral.splitit.training.server.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val config =
            CorsConfiguration().apply {
                allowedOrigins =
                    listOf(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000",
                    )
                allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
                allowedHeaders = listOf("*")
                allowCredentials = true
                maxAge = 3600
            }
        val source =
            UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", config)
            }
        return CorsFilter(source)
    }
}
