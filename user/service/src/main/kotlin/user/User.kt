package waffle.guam.user.service.user

import waffle.guam.user.infra.db.UserEntity

data class User(
    val id: Long,
    val email: String?,
    val nickname: String,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val profileImage: String?,
    val interests: List<Interest>,
) {
    val isProfileSet: Boolean get() = nickname.isNotBlank()
}

data class Interest(
    val name: String,
)

fun User(entity: UserEntity): User = User(
    id = entity.id,
    email = entity.email,
    nickname = entity.nickname,
    introduction = entity.introduction,
    githubId = entity.githubId,
    blogUrl = entity.blogUrl,
    profileImage = entity.profileImage,
    interests = entity.interests.map { Interest(it) }
)
