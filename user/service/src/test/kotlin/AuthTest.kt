package waffle.guam.user.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.service.auth.AuthCommandService
import waffle.guam.user.service.auth.AuthCommandService.CreateUser

@ServiceTest
class AuthTest @Autowired constructor(
    private val authCommandService: AuthCommandService,
) {

    @Transactional
    @Test
    fun create() {
        val command = CreateUser("test")

        assertDoesNotThrow {
            authCommandService.createUser(command)
        }

        assertThrows<DuplicateUser> {
            authCommandService.createUser(command)
        }
    }
}
