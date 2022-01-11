package waffle.guam.community.service

import org.springframework.http.HttpStatus

abstract class GuamException(message: String, val status: HttpStatus) : Exception(message)

open class GuamBadRequest(message: String, status: HttpStatus = HttpStatus.BAD_REQUEST) : GuamException(message, status)

open class GuamUnAuthorized(message: String, status: HttpStatus = HttpStatus.UNAUTHORIZED) : GuamException(message, status)

open class GuamForbidden(message: String, status: HttpStatus = HttpStatus.FORBIDDEN) : GuamException(message, status)

open class GuamNotFound(message: String, status: HttpStatus = HttpStatus.NOT_FOUND) : GuamException(message, status)

open class GuamConflict(message: String, status: HttpStatus = HttpStatus.CONFLICT) : GuamException(message, status)
