package waffle.guam.community.common

import waffle.guam.community.data.jdbc.user.UserEntity

data class UserContext(
    val id: Long
) {
    companion object {
        fun of(e: UserEntity): UserContext {
//            Name Not Set 관련 논의 필요
//            e.checkName()
            return UserContext(id = e.id)
        }
    }
}
