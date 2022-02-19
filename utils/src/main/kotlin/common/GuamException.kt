package waffle.guam.community.common

abstract class GuamException(message: String, val status: Int) : RuntimeException(message)

open class GuamBadRequest(message: String) : GuamException(message, 400)

open class GuamUnAuthorized(message: String) : GuamException(message, 401)

open class GuamForbidden(message: String) : GuamException(message, 403)

open class GuamNotFound(message: String) : GuamException(message, 404)

open class GuamConflict(message: String) : GuamException(message, 409)
