package waffle.guam.community.common

import waffle.guam.community.data.jdbc.user.UserEntity

data class UserContext(
    val id: Long
)

fun UserContext(e: UserEntity): UserContext =
    UserContext(id = e.id)
