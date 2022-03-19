package waffle.guam.community.service.domain.user

import waffle.guam.community.data.jdbc.interest.name
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.UserId

data class User(
    val id: UserId,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val nickname: String?,
    val email: String?,
    val profileImage: String?,
    val interests: List<Interest>,
) {
    val isProfileSet: Boolean get() {
        return !nickname.isNullOrBlank()
    }

    data class Interest(val name: String)
}

fun User(e: UserEntity): User =
    User(
        id = e.id,
        introduction = e.introduction,
        githubId = e.githubId,
        blogUrl = e.blogUrl,
        nickname = e.nickname,
        email = e.email,
        profileImage = e.profileImage,
        interests = e.interests.map { User.Interest(it.name) }
    )

fun AnonymousUser(suffix: String = ""): User {
    return User(
        id = 0,
        nickname = "익명$suffix",
        introduction = null,
        githubId = null,
        blogUrl = null,
        email = null,
        profileImage = null,
        interests = listOf(),
    )
}
