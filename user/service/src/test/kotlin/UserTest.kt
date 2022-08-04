package waffle.guam.user.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.infra.aws.ImageClient
import waffle.guam.user.infra.aws.ProfileImage
import waffle.guam.user.infra.db.UserEntity
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.user.User
import waffle.guam.user.service.user.UserCommandService
import waffle.guam.user.service.user.UserCommandService.UpdateUser

@ServiceTest
class UserTest @Autowired constructor(
    private val userCommandService: UserCommandService,
    private val userRepository: UserRepository,
) {
    @MockBean
    lateinit var imageClient: ImageClient

    @Transactional
    @Test
    fun update() {
        val mockImage =
            MockMultipartFile("test", null, "image/jpeg", javaClass.getResourceAsStream("test.jpeg"))

        fun updateUser(userId: Long): User {
            return userCommandService.updateUser(
                UpdateUser(
                    userId = userId,
                    nickname = "nickname",
                    introduction = null,
                    githubId = "githubId",
                    blogUrl = "blogUrl",
                    profileImage = mockImage
                )
            )
        }

        whenever(imageClient.upload(1L, mockImage))
            .thenReturn(ProfileImage("/test/1.jpeg"))

        assertThrows<UserNotFound> {
            updateUser(1L)
        }

        val user = userRepository.save(UserEntity())

        updateUser(1L).run {
            assertThat(id).isEqualTo(user.id)
            assertThat(nickname).isEqualTo("nickname")
            assertThat(introduction).isEqualTo(null)
            assertThat(githubId).isEqualTo("githubId")
            assertThat(blogUrl).isEqualTo("blogUrl")
            assertThat(profileImage).isEqualTo("test/1.jpeg")
        }
    }
}
