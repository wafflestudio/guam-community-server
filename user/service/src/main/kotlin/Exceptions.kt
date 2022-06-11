package waffle.guam.user.service

import org.springframework.http.HttpStatus

abstract class UserException : RuntimeException() {
    abstract val msg: String
    abstract val status: HttpStatus
}

class UnAuthorized : UserException() {
    override val msg: String = "권한이 없습니다."
    override val status: HttpStatus = HttpStatus.UNAUTHORIZED
}

class UserNotFound : UserException() {
    override val msg: String = "해당 유저가 존재하지 않습니다."
    override val status: HttpStatus = HttpStatus.NOT_FOUND
}

class DuplicateUser : UserException() {
    override val msg: String = "이미 존재하는 유저입니다."
    override val status: HttpStatus = HttpStatus.CONFLICT
}

class InterestNotFound : UserException() {
    override val msg: String = "해당 관심사가 존재하지 않습니다."
    override val status: HttpStatus = HttpStatus.NOT_FOUND
}

class DuplicateInterest : UserException() {
    override val msg: String = "이미 존재하는 관심사입니다."
    override val status: HttpStatus = HttpStatus.CONFLICT
}
