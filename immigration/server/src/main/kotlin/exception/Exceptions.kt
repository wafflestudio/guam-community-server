package waffle.guam.immigration.server.exception

import org.springframework.http.HttpStatus

open class ImmigrationException(msg: String, val status: HttpStatus) : Exception(msg)

class MissingHeaderException(msg: String = "인증 정보가 제공되지 않았습니다.") : ImmigrationException(msg, HttpStatus.UNAUTHORIZED)

class UserNotFound(msg: String = "유저를 찾을 수 없습니다.") : ImmigrationException(msg, HttpStatus.NOT_FOUND)
