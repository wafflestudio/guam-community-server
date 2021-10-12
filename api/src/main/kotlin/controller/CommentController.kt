package waffle.guam.community.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.service.command.CommandDispatcher
import waffle.guam.community.service.command.Event
import waffle.guam.community.service.command.comment.CreateComment

@RestController
class CommentController(
    private val dispatcher: CommandDispatcher
) {
    @GetMapping("")
    fun test() =
        when (val e = dispatcher.dispatch(CreateComment("a"))) {
            is Event.Result -> e.value
            is Event.Error -> throw e.error
        }
}
