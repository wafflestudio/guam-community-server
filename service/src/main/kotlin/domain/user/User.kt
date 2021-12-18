package waffle.guam.community.service.domain.user

import waffle.guam.community.data.jdbc.stack.name
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.UserId

data class User(
    val id: UserId,
    val firebaseUid: String,
    val nickname: String?,
    val email: String?,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val profileImage: String?,
    val stacks: List<Stack>,
) {
    companion object {
        fun of(e: UserEntity): User =
            User(
                id = e.id,
                firebaseUid = e.firebaseUid,
                introduction = e.introduction,
                nickname = e.nickname,
                email = e.email,
                githubId = e.githubId,
                blogUrl = e.blogUrl,
                profileImage = e.profileImage,
                stacks = e.stacks.map { Stack(it.name) }
            )
    }
}

data class Stack(val name: String)
