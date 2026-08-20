package edu.austral.splitit.training.server.infrastructure.persistence.entities

import edu.austral.splitit.training.server.TEST_EMAIL
import edu.austral.splitit.training.server.TEST_PASSWORD_HASH
import edu.austral.splitit.training.server.TEST_USER_ID
import edu.austral.splitit.training.server.associate
import edu.austral.splitit.training.server.domain.model.UserRole
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserEntityMapperTest {
    @Test
    fun shouldMapEntityToDomain() {
        val entity =
            UserEntity(
                id = TEST_USER_ID,
                email = TEST_EMAIL,
                passwordHash = TEST_PASSWORD_HASH,
                role = UserRole.ASSOCIATE,
            )

        assertEquals(associate(), entity.toDomain())
    }

    @Test
    fun shouldMapDomainToEntity() {
        val entity = associate().toEntity()

        assertEquals(TEST_USER_ID, entity.id)
        assertEquals(TEST_EMAIL, entity.email)
        assertEquals(TEST_PASSWORD_HASH, entity.passwordHash)
        assertEquals(UserRole.ASSOCIATE, entity.role)
    }
}
