package waffle.guam.community.service.command.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import waffle.guam.community.Log
import waffle.guam.community.common.UserNotFound
import waffle.guam.community.data.jdbc.interest.name
import waffle.guam.community.data.jdbc.user.UserApiRepository
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.command.Command
import waffle.guam.community.service.command.CommandHandler
import waffle.guam.community.service.command.Result
import waffle.guam.community.service.command.image.UploadImageList
import waffle.guam.community.service.command.image.UploadImageListHandler
import waffle.guam.community.service.domain.image.ImageType
import waffle.guam.community.service.domain.user.User

@Service
class UpdateUserHandler(
    private val userAPIRepository: UserApiRepository,
    private val imageHandler: UploadImageListHandler,
) : CommandHandler<UpdateUser, UserUpdated> {
    companion object : Log

    @Transactional
    override fun handle(command: UpdateUser): UserUpdated {
        val userEntity = userAPIRepository.find(command.userId) ?: throw UserNotFound(command.userId)
        userEntity.updateBy(command)
        return UserUpdated(userEntity)
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
    val id: Long,
    val nickname: String?,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val email: String?,
    val profileImage: String?,
    val interests: List<User.Interest>,
) : Result

fun UserUpdated(e: UserEntity) =
    UserUpdated(
        id = e.id,
        nickname = e.nickname,
        introduction = e.introduction,
        githubId = e.githubId,
        blogUrl = e.blogUrl,
        email = e.email,
        profileImage = e.profileImage,
        interests = e.interests.map { User.Interest(it.name) },
    )
