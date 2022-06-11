package waffle.guam.user.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import waffle.guam.user.infra.firebase.FirebaseClient.FirebaseTokenExpired
import waffle.guam.user.service.UserException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [UserException::class])
    fun domainException(e: UserException) = ResponseEntity(e.msg, e.status)

    @ExceptionHandler(value = [FirebaseTokenExpired::class])
    fun authException(e: FirebaseTokenExpired) = ResponseEntity("", HttpStatus.UNAUTHORIZED)
}
