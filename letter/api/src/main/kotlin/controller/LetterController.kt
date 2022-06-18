package waffle.guam.letter.api.controller

import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.service.command.CreateLetter
import waffle.guam.favorite.service.command.EmptyLetterBox
import waffle.guam.favorite.service.command.LetterCommandService
import waffle.guam.favorite.service.command.ReadLetterBox
import waffle.guam.favorite.service.domain.Letter
import waffle.guam.favorite.service.domain.LetterBoxPreview
import waffle.guam.favorite.service.query.LetterQueryService

@RequestMapping("/api/v1/letters")
@RestController
class LetterController(
    private val letterCommandService: LetterCommandService,
    private val letterQueryService: LetterQueryService,
) {

    @GetMapping("")
    suspend fun getLetterBoxPreviews(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): LetterBoxResponse {
        return LetterBoxResponse(
            userId = userId,
            letterBoxes = letterQueryService.getLetterBoxPreviews(userId)
        )
    }

    @GetMapping("/{pairId}")
    suspend fun getLetters(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable pairId: Long,
        @RequestParam(defaultValue = "50") size: Int,
        @RequestParam(required = false) beforeLetterId: Long?,
    ): LetterResponse {
        val letterBox = letterQueryService.getLetterBox(
            userId = userId,
            pairId = pairId,
            size = size,
            letterIdSmallerThan = beforeLetterId
        )?.let {
            // 조회 시, 모든 쪽지를 읽음 처리
            letterCommandService.readLetterBox(ReadLetterBox(userId = userId, letterBox = it))
        }

        return LetterResponse(
            userId = userId,
            pairId = pairId,
            letters = letterBox?.letters ?: emptyList()
        )
    }

    @PostMapping("")
    suspend fun sendLetter(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @ModelAttribute request: SendLetterRequest,
    ): Letter {
        return letterCommandService.createLetter(
            CreateLetter(
                senderId = userId,
                receiverId = request.to,
                text = request.text,
                images = request.image
            )
        )
    }

    @DeleteMapping("/{pairId}")
    suspend fun deleteLetterBox(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable pairId: Long,
    ) {
        return letterCommandService.emptyLetterBox(
            EmptyLetterBox(
                userId = userId,
                pairId = pairId
            )
        )
    }
}

data class LetterBoxResponse(
    val userId: Long,
    val letterBoxes: List<LetterBoxPreview>,
)

data class LetterResponse(
    val userId: Long,
    val pairId: Long,
    val letters: List<Letter>,
)

data class SendLetterRequest(
    val to: Long,
    val text: String,
    val image: List<FilePart>?,
)
