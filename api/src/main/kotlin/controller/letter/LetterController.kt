package waffle.guam.community.controller.letter

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.community.common.UserContext
import waffle.guam.community.controller.letter.req.SendLetterRequest
import waffle.guam.community.service.command.letter.CreateLetter
import waffle.guam.community.service.command.letter.CreateLetterHandler
import waffle.guam.community.service.command.letter.DeleteLetterBox
import waffle.guam.community.service.command.letter.DeleteLetterBoxHandler
import waffle.guam.community.service.query.letter.displayer.LetterBoxDisplayer

@RequestMapping("/api/v1/letters")
@RestController
class LetterController(
    private val createLetterHandler: CreateLetterHandler,
    private val deleteLetterHandler: DeleteLetterBoxHandler,
    private val letterBoxDisplayer: LetterBoxDisplayer,
) {
    @PostMapping("")
    fun sendLetter(
        userContext: UserContext,
        @ModelAttribute req: SendLetterRequest,
    ) = createLetterHandler.handle(
        CreateLetter(
            from = userContext.id,
            to = req.to,
            text = req.text,
            image = req.image,
        )
    )

    @GetMapping("")
    fun getLetterBoxes(
        userContext: UserContext,
    ) = letterBoxDisplayer.getMyLetterBoxes(userContext.id)

    @GetMapping("/{pairId}")
    fun getLetters(
        userContext: UserContext,
        @PathVariable pairId: Long,
        @RequestParam(defaultValue = "50") size: Long,
        @RequestParam(required = false) beforeLetterId: Long?,
    ) = letterBoxDisplayer.getLetters(userContext.id, pairId, beforeLetterId, size)

    @DeleteMapping("/{pairId}")
    fun deleteLetterBox(
        userContext: UserContext,
        @PathVariable pairId: Long,
    ) = deleteLetterHandler.handle(DeleteLetterBox(pairId = pairId, userId = userContext.id))
}
