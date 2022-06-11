package waffle.guam.user.api.controller

import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.ReadNotification

data class ReadNotificationRequest(
    val userId: Long,
    val pushEventIds: List<Long>,
)

data class CreateNotificationRequest(
    val producerId: Long,
    val infos: List<Info>,
) {
    data class Info(
        val consumerId: Long,
        val kind: String,
        val body: String,
        val linkUrl: String,
        val isAnonymousEvent: Boolean,
    )
}

fun ReadNotification(request: ReadNotificationRequest) = ReadNotification(
    userId = request.userId,
    notificationIds = request.pushEventIds
)

fun CreateNotification(request: CreateNotificationRequest) = CreateNotification(
    producerId = 0,
    infos = request.infos.map {
        CreateNotification.Info(
            consumerId = it.consumerId,
            kind = it.kind,
            body = it.body,
            linkUrl = it.linkUrl,
            isAnonymousEvent = it.isAnonymousEvent
        )
    }
)
