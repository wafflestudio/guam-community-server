package waffle.guam.user.api.request

import waffle.guam.user.service.notification.NotificationCommandService.ReadNotification

data class ReadNotificationRequest(
    val userId: Long,
    val pushEventIds: List<Long>,
)

fun ReadNotification(request: ReadNotificationRequest) = ReadNotification(
    userId = request.userId,
    notificationIds = request.pushEventIds
)
