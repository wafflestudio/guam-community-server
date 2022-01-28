package waffle.guam.community.controller.letter

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.letter.req.SendLetterRequest
import waffle.guam.community.service.LetterId
import waffle.guam.community.service.command.letter.CreateLetter
import waffle.guam.community.service.command.letter.CreateLetterHandler
import waffle.guam.community.service.command.letter.DeleteLetter
import waffle.guam.community.service.command.letter.DeleteLetterHandler

@RequestMapping("/api/v1/letters")
@RestController
class LetterController(
    private val createLetterHandler: CreateLetterHandler,
    private val deleteLetterHandler: DeleteLetterHandler,
) {
    @PostMapping("")
    fun sendLetter(
        userContext: UserContext,
        @RequestBody req: SendLetterRequest,
    ) = createLetterHandler.handle(
        CreateLetter(
            from = userContext.id,
            to = req.receiverId,
            text = req.text,
            image = req.image,
        )
    )

    @DeleteMapping("/{letterId}")
    fun deleteLetter(
        userContext: UserContext,
        @PathVariable letterId: LetterId,
    ) = deleteLetterHandler.handle(DeleteLetter(letterId, userContext.id))

    @GetMapping("/my/received")
    fun myLetters(
        userContext: UserContext,
    ) {
    }
}
