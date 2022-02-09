package waffle.guam.community.service.command.user

import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.Log
import waffle.guam.community.data.jdbc.user.UserAPIRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.UserNotFound
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType
import java.time.Instant

@Service
class UpdateUserHandler(
    private val userAPIRepository: UserAPIRepository,
    private val imageHandler: UploadImageListHandler,
) : CommandHandler<UpdateUser, UserUpdated> {
    companion object : Log

    @Transactional
    override fun handle(command: UpdateUser): UserUpdated {
        val userEntity = userAPIRepository.find(command.userId) ?: throw UserNotFound(command.userId)
        userEntity.updateBy(command)
        return UserUpdated(userEntity)
    }

    @Transactional
    fun updateToken(userId: Long, newDeviceToken: String): UserDeviceTokenUpdated {
        val userEntity = userAPIRepository.find(userId) ?: throw UserNotFound(userId)
//        userEntity.deviceToken = newDeviceToken Todo Immigration으로 옮기기
        return UserDeviceTokenUpdated(userId, newDeviceToken)
    }

    private fun UserEntity.updateBy(cmd: UpdateUser) {
        nickname = cmd.nickname ?: nickname
        introduction = cmd.introduction ?: introduction
        githubId = cmd.githubId ?: githubId
        blogUrl = cmd.blogUrl ?: blogUrl
        profileImage = cmd.profileImage?.let { img ->
            val images = imageHandler.handle(UploadImageList(id, ImageType.PROFILE, listOf(img)))
            images.imagePaths.first() // TODO 업데이트 시 이미지 삭제
        }
    }

    @EventListener
    fun userDeviceTokenUpdated(event: UserDeviceTokenUpdated) {
        log.info("DEVICE TOKEN UPDATED(USER=${event.userId}, TOKEN=${event.newDeviceToken}): ${Instant.now()}")
    }
}

data class UpdateUser(
    val userId: Long,
    val nickname: String?,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val profileImage: MultipartFile?,
) : Command

data class UserUpdated(
    val userId: Long,
    val nickname: String?,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
) : Result

fun UserUpdated(e: UserEntity) =
    UserUpdated(
        userId = e.id,
        nickname = e.nickname,
        introduction = e.introduction,
        githubId = e.githubId,
        blogUrl = e.blogUrl,
    )

data class UserDeviceTokenUpdated(
    val userId: Long,
    val newDeviceToken: String,
) : Result
