package edu.austral.splitit.training.server.infrastructure.api

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UserResponseTest {
    @Test
    fun shouldMapPersistedUser() {
        val response = associate().toUserResponse()

        assertEquals(associate().id, response.id)
        assertEquals(TEST_EMAIL, response.email)
        assertEquals(UserRole.ASSOCIATE, response.role)
    }

    @Test
    fun shouldFailIfUserHasNoId() {
        assertFailsWith<IllegalArgumentException> {
            associate(id = null).toUserResponse()
        }
    }
}
