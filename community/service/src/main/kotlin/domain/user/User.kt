package waffle.guam.community.service.domain.user

import waffle.guam.user.domain.UserInfo

fun AnonymousUser(suffix: String = ""): UserInfo {
    return UserInfo(
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
