package waffle.guam.community.service.domain.user

import waffle.guam.community.service.UserId

data class User(
    val id: UserId,
    val username: String
)
