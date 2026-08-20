package edu.austral.splitit.training.server

import edu.austral.splitit.training.server.domain.model.User
import edu.austral.splitit.training.server.domain.model.UserRole

const val TEST_EMAIL = "socio@mail.com"
const val TEST_PASSWORD = "secret123"
const val TEST_PASSWORD_HASH = "hashed-password"
const val TEST_TOKEN = "jwt-token"
const val TEST_USER_ID = 1L

fun associate(
    id: Long? = TEST_USER_ID,
    email: String = TEST_EMAIL,
    passwordHash: String = TEST_PASSWORD_HASH,
): User =
    User(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = UserRole.ASSOCIATE,
    )

fun librarian(
    id: Long? = TEST_USER_ID,
    email: String = "librarian@mail.com",
    passwordHash: String = TEST_PASSWORD_HASH,
): User =
    User(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = UserRole.LIBRARIAN,
    )
