package edu.austral.splitit.training.server.infrastructure.security

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_TOKEN
import edu.austral.splitit.training.server.TEST_USER_ID
import edu.austral.splitit.training.server.application.port.AuthenticatedUser
import edu.austral.splitit.training.server.application.port.TokenProvider
import edu.austral.splitit.training.server.domain.model.UserRole
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JwtAuthenticationFilterTest {
    private val tokenProvider = mock<TokenProvider>()
    private val filter = JwtAuthenticationFilter(tokenProvider)
    private val filterChain = mock<FilterChain>()

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun shouldSetAuthenticationWhenBearerTokenIsValid() {
        val request = MockHttpServletRequest()
        request.addHeader(HttpHeaders.AUTHORIZATION, "$BEARER_PREFIX$TEST_TOKEN")
        whenever(tokenProvider.parse(TEST_TOKEN)).thenReturn(
            AuthenticatedUser(id = TEST_USER_ID, email = TEST_EMAIL, role = UserRole.ASSOCIATE),
        )

        filter.doFilter(request, MockHttpServletResponse(), filterChain)

        val authentication = assertNotNull(SecurityContextHolder.getContext().authentication)
        assertEquals(
            AuthenticatedUser(id = TEST_USER_ID, email = TEST_EMAIL, role = UserRole.ASSOCIATE),
            authentication.principal,
        )
        assertTrue(authentication.authorities.any { it.authority == "ROLE_ASSOCIATE" })
        verify(filterChain).doFilter(eq(request), any())
    }

    @Test
    fun shouldNotSetAuthenticationWhenHeaderIsMissing() {
        filter.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
        verify(tokenProvider, never()).parse(any())
        verify(filterChain).doFilter(any(), any())
    }

    @Test
    fun shouldNotSetAuthenticationWhenTokenIsInvalid() {
        val request = MockHttpServletRequest()
        request.addHeader(HttpHeaders.AUTHORIZATION, "$BEARER_PREFIX$TEST_TOKEN")
        whenever(tokenProvider.parse(TEST_TOKEN)).thenReturn(null)

        filter.doFilter(request, MockHttpServletResponse(), filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
        verify(filterChain).doFilter(eq(request), any())
    }
}
