package waffle.guam.community.service.command

import waffle.guam.community.service.client.NotificationRequest

interface EventResult

interface NotifyingEventResult : EventResult {
    fun toRequest(): NotificationRequest
}
