package waffle.guam.immigration.server.user

import waffle.guam.immigration.server.user.domain.User

internal typealias ApiUser = waffle.guam.immigration.api.user.User

fun ApiUser(user: User): ApiUser = user.run { ApiUser(id, deviceId) }
