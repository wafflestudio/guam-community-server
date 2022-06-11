package waffle.guam.community.service.domain.user

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
    val isProfileSet: Boolean
        get() {
            return !nickname.isNullOrBlank()
        }

    data class Interest(val name: String)
}

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
