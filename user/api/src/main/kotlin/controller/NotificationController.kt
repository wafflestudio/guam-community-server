package waffle.guam.user.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.user.service.UnAuthorized
import waffle.guam.user.service.notification.Notification
import waffle.guam.user.service.notification.NotificationCommandService
import waffle.guam.user.service.notification.NotificationQueryService
import waffle.guam.user.service.notification.NotificationsPage

@RequestMapping("/api/v1/push")
@RestController
class NotificationController(
    private val queryService: NotificationQueryService,
    private val commandService: NotificationCommandService,
) {
    @GetMapping("")
    fun getNotifications(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @RequestParam page: Int,
        @RequestParam size: Int,
    ): NotificationsPage {
        return queryService.getNotificationList(
            userId = userId,
            page = page,
            size = size
        )
    }

    @PostMapping("/create")
    fun createNotifications(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @RequestBody request: CreateNotificationRequest,
    ): List<Notification> {
        if (userId != request.producerId) {
            throw UnAuthorized()
        }

        return commandService.create(
            command = CreateNotification(request)
        )
    }

    @PostMapping("/read")
    fun readNotifications(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @RequestBody request: ReadNotificationRequest,
    ): List<Notification> {
        if (userId != request.userId) {
            throw UnAuthorized()
        }

        return commandService.read(
            command = ReadNotification(request)
        )
    }
}
