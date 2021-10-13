package waffle.guam.community.service.domain.user

import waffle.guam.community.data.jdbc.user.UserEntity

data class User(
    val id: Long,
    val username: String
) {
    companion object {
        fun of(e: UserEntity) = User(
            id = e.id,
            username = e.username
        )
    }
}
