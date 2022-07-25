package waffle.guam.user.api.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import waffle.guam.user.service.UnAuthorized
import waffle.guam.user.service.UserNotFound
import waffle.guam.user.service.user.Interest
import waffle.guam.user.service.user.User
import waffle.guam.user.service.user.UserCommandService
import waffle.guam.user.service.user.UserCommandService.CreateInterest
import waffle.guam.user.service.user.UserCommandService.DeleteInterest
import waffle.guam.user.service.user.UserCommandService.UpdateUser
import waffle.guam.user.service.user.UserQueryService
import javax.validation.Valid

@RequestMapping("/api/v1/users")
@RestController
class UserController(
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
) {

    @GetMapping("/me")
    fun getMe(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
    ): User {
        return userQueryService.getUser(userId) ?: throw UserNotFound()
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ): User {
        return userQueryService.getUser(userId) ?: throw UserNotFound()
    }

    @GetMapping("")
    fun getUsers(
        @RequestParam userIds: List<Long>,
    ): List<User> {
        return userQueryService.getUsers(userIds)
    }

    @PatchMapping("/{targetUserId}")
    fun updateUser(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable targetUserId: Long,
        @Valid @ModelAttribute request: UpdateUserRequest,
    ): User {
        if (userId != targetUserId) {
            throw UnAuthorized()
        }

        return userCommandService.updateUser(
            UpdateUser(
                userId = targetUserId,
                nickname = request.nickname,
                introduction = request.introduction,
                githubId = request.githubId,
                blogUrl = request.blogUrl,
                profileImage = request.profileImage
            )
        )
    }

    // 프로필 이미지 초기화용 api
    @PatchMapping("/{targetUserId}/json")
    fun updateUserJson(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable targetUserId: Long,
        @Valid @RequestBody request: UpdateUserRequest,
    ): User {
        if (userId != targetUserId) {
            throw UnAuthorized()
        }

        return userCommandService.updateUser(
            UpdateUser(
                userId = targetUserId,
                nickname = request.nickname,
                introduction = request.introduction,
                githubId = request.githubId,
                blogUrl = request.blogUrl,
                clearImage = true
            )
        )
    }

    @PostMapping("{targetUserId}/interest")
    fun addInterest(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable targetUserId: Long,
        @RequestBody request: CreateInterestRequest,
    ): User {
        if (userId != targetUserId) {
            throw UnAuthorized()
        }

        return userCommandService.createInterest(
            CreateInterest(
                userId = targetUserId,
                interest = Interest(name = request.name)
            )
        )
    }

    @DeleteMapping("{targetUserId}/interest")
    fun deleteInterest(
        @RequestHeader("X-GATEWAY-USER-ID") userId: Long,
        @PathVariable targetUserId: Long,
        @RequestParam(required = true) name: String, // 기존 api가 @RequestParam으로 받고 있음..
    ): User {
        if (userId != targetUserId) {
            throw UnAuthorized()
        }

        return userCommandService.deleteInterest(
            DeleteInterest(
                userId = targetUserId,
                interest = Interest(name = name)
            )
        )
    }
}
