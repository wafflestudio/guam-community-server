package waffle.guam.community.controller.push

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.GuamUnAuthorized
import waffle.guam.community.common.UserContext
import waffle.guam.community.service.command.notification.PushEventsRead
import waffle.guam.community.service.command.notification.ReadPushEventHandler
import waffle.guam.community.service.command.notification.ReadPushEvents
import waffle.guam.community.service.query.push.PushEventListCollector

@RequestMapping("/api/v1/push")
@RestController
class PushEventController(
    private val pushEventListListCollector: PushEventListCollector,
    private val readPushEventHandler: ReadPushEventHandler,
) {

    @GetMapping("")
    fun getPushEvents(userContext: UserContext) = pushEventListListCollector.get(userContext.id)

    @PostMapping("/read")
    fun readPushEvents(
        userContext: UserContext,
        request: ReadPushEvents,
    ): PushEventsRead {
        if (userContext.id != request.userId) {
            throw GuamUnAuthorized("")
        }

        return readPushEventHandler.handle(request)
    }
}
