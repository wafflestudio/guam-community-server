package waffle.guam.community.command.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.jdbc.Sql
import waffle.guam.community.data.jdbc.user.UserRepository
import waffle.guam.community.service.command.user.CreateUser
import waffle.guam.community.service.command.user.CreateUserHandler

@DataJpaTest
@Sql("classpath:/command/user/data.sql")
class UserCreateHandlerTest @Autowired constructor(
    private val userRepository: UserRepository,
) {
    private val createUserHandler = CreateUserHandler(userRepository)

    @DisplayName("유저를 생성할 수 있다")
    @Test
    fun createUser() {
        // given data.sql

        // when
        createUserHandler.handle(CreateUser(3L))

        // then
        val result = userRepository.getById(3L)
        assertThat(result.id).isEqualTo(3L)
    }
}
