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
import waffle.guam.letter.service.command.ClearLetterBox
import waffle.guam.letter.service.command.CreateLetter
import waffle.guam.letter.service.command.LetterCommandService
import waffle.guam.letter.service.command.ReadLetterBox
import waffle.guam.letter.service.domain.Letter
import waffle.guam.letter.service.domain.LetterBoxPreview
import waffle.guam.letter.service.query.LetterQueryService
import waffle.guam.letter.api.config.BlockFilter
import waffle.guam.letter.data.r2dbc.repository.LetterRepository

@RequestMapping("/api/v1/letters")
@RestController
class LetterController(
    private val letterCommandService: LetterCommandService,
    private val letterQueryService: LetterQueryService,
    private val letterRepository: LetterRepository
) {

    @GetMapping("/me")
    suspend fun getMe(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): LetterMeResponse {
        return LetterMeResponse(
            unRead = letterRepository.countBySentToAndIsRead(userId = userId, isRead = false)
        )
    }

    @GetMapping("")
    suspend fun getLetterBoxPreviews(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        blockedIds: BlockFilter,
    ): LetterBoxResponse {
        return LetterBoxResponse(
            userId = userId,
            letterBoxes = letterQueryService.getLetterBoxPreviews(userId, blockedIds.blockedPairs)
        )
    }

    @GetMapping("/{pairId}")
    suspend fun getLetters(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable pairId: Long,
        @RequestParam(defaultValue = "50") size: Int,
        @RequestParam(required = false) beforeLetterId: Long?,
    ): LetterListResponse {
        // read all
        letterCommandService.readLetterBox(ReadLetterBox(userId = userId, pairId = pairId))

        val letterBox = letterQueryService.getLetterBox(
            userId = userId,
            pairId = pairId,
            size = size,
            letterIdSmallerThan = beforeLetterId
        )

        return LetterListResponse(
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
                images = request.image?.takeIf { it.isNotEmpty() }
            )
        )
    }

    @DeleteMapping("/{pairId}")
    suspend fun clearLetterBox(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable pairId: Long,
    ) {
        return letterCommandService.clearLetterBox(
            ClearLetterBox(
                userId = userId,
                pairId = pairId
            )
        )
    }
}

data class LetterMeResponse(
    val unRead: Int
)

data class LetterBoxResponse(
    val userId: Long,
    val letterBoxes: List<LetterBoxPreview>,
)

data class LetterListResponse(
    val userId: Long,
    val pairId: Long,
    val letters: List<Letter>,
)

data class SendLetterRequest(
    val to: Long,
    val text: String,
    val image: List<FilePart>?,
)
