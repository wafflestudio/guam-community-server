package waffle.guam.community.controller.letter

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.letter.req.SendLetterRequest
import waffle.guam.community.service.command.letter.CreateLetter
import waffle.guam.community.service.command.letter.CreateLetterHandler
import waffle.guam.community.service.command.letter.DeleteLetter
import waffle.guam.community.service.command.letter.DeleteLetterHandler
import waffle.guam.community.service.query.letter.displayer.LetterBoxDisplayer

@RequestMapping("/api/v1/letters")
@RestController
class LetterController(
    private val createLetterHandler: CreateLetterHandler,
    private val deleteLetterHandler: DeleteLetterHandler,
    private val letterBoxDisplayer: LetterBoxDisplayer,
) {
    @PostMapping("")
    fun sendLetter(
        userContext: UserContext,
        @RequestBody req: SendLetterRequest,
    ) = createLetterHandler.handle(
        CreateLetter(
            from = userContext.id,
            to = req.to,
            text = req.text,
            image = req.image,
        )
    )

    @DeleteMapping("/{letterId}")
    fun deleteLetter(
        userContext: UserContext,
        @PathVariable letterId: Long,
    ) = deleteLetterHandler.handle(DeleteLetter(letterId = letterId, userId = userContext.id))

    @GetMapping("/letters")
    fun getLetterBoxes(
        userContext: UserContext,
    ) = letterBoxDisplayer.getMyLetterBoxes(userContext.id)

    @GetMapping("/letters/{pairId}")
    fun getLetters(
        userContext: UserContext,
        @PathVariable pairId: Long,
        @RequestParam(defaultValue = "0") afterLetterId: Long,
        @RequestParam(defaultValue = "50") size: Long,
    ) = letterBoxDisplayer.getLetters(userContext.id, pairId, afterLetterId, size)
}
