package waffle.guam.user.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import waffle.guam.user.infra.db.UserEntity
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.notification.NotificationCommandService
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.ReadNotification
import javax.transaction.Transactional

@ServiceTest
class NotificationTest @Autowired constructor(
    private val commandSerivce: NotificationCommandService,
    private val userRepository: UserRepository,
) {

    @Transactional
    @Test
    fun createAndRead() {
        val info = CreateNotification.Info(
            consumerId = 400L,
            kind = "POST_LIKE",
            body = "test",
            linkUrl = "gg.gg",
            isAnonymousEvent = false
        )
        val command = CreateNotification(
            producerId = 1L,
            infos = listOf(info)
        )

        assertThrows<UserNotFound> {
            commandSerivce.create(command)
        }

        val user = userRepository.save(UserEntity())

        val notification = commandSerivce.create(command.copy(producerId = user.id))
            .first()
            .apply {
                assertThat(writer.id).isEqualTo(user.id)
                assertThat(userId).isEqualTo(info.consumerId)
                assertThat(kind).isEqualTo(info.kind)
                assertThat(linkUrl).isEqualTo(info.linkUrl)
                assertThat(isRead).isEqualTo(false)
            }

        commandSerivce.read(ReadNotification(info.consumerId, listOf(notification.id)))
            .first()
            .apply {
                assertThat(id).isEqualTo(notification.id)
                assertThat(isRead).isEqualTo(true)
            }
    }
}
