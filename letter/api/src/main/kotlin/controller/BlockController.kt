package waffle.guam.letter.api.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.favorite.service.command.BlockCommandService
import waffle.guam.favorite.service.command.CreateBlock
import waffle.guam.favorite.service.domain.Block

@RequestMapping("/api/v1/block")
@RestController
class BlockController(
    private val blockCommandService: BlockCommandService,
) {
    @PostMapping("")
    suspend fun report(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        request: CreateBlockRequest,
    ): Block {
        return blockCommandService.createBlock(
            CreateBlock(userId = userId, targetId = request.targetId)
        )
    }
}

data class CreateBlockRequest(
    val targetId: Long,
)
