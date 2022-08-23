package waffle.guam.user.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import waffle.guam.user.api.request.CreateBlockRequest
import waffle.guam.user.api.request.DeleteBlockRequest
import waffle.guam.user.service.block.Block
import waffle.guam.user.service.block.BlockCommandService
import waffle.guam.user.service.block.BlockCommandService.CreateBlock
import waffle.guam.user.service.block.BlockCommandService.DeleteBlock
import waffle.guam.user.service.block.BlockList
import waffle.guam.user.service.block.BlockQueryService

@RequestMapping("/api/v1/blocks")
@RestController
class BlockController(
    private val blockCommandService: BlockCommandService,
    private val blockQueryService: BlockQueryService,
) {

    @GetMapping("")
    fun getBlocks(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): BlockList {
        return blockQueryService.getBlockList(userId)
    }

    @PostMapping("")
    fun create(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @RequestBody request: CreateBlockRequest,
    ): Block {
        return blockCommandService.createBlock(
            CreateBlock(userId = userId, blockUserId = request.blockUserId)
        )
    }

    @DeleteMapping("")
    fun delete(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @RequestBody request: DeleteBlockRequest,
    ) {
        return blockCommandService.deleteBlock(
            DeleteBlock(userId = userId, blockUserId = request.blockUserId)
        )
    }
}
