package waffle.guam.community.service.domain.user

import waffle.guam.community.data.jdbc.stack.name
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.UserId

data class User(
    val id: UserId,
    val firebaseUid: String,
    val nickname: String?,
    val email: String?,
    val stacks: List<Stack>,
) {
    data class Stack(val name: String)
}

fun User(e: UserEntity) =
    User(
        id = e.id,
        firebaseUid = e.firebaseUid,
        nickname = e.nickname,
        email = e.email,
        stacks = e.stacks.map { User.Stack(it.name) }
    )
