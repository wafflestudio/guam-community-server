package waffle.guam.user.notification.event

import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification

interface NotifyingEvent {
    fun toRequest(): CreateNotification
}
