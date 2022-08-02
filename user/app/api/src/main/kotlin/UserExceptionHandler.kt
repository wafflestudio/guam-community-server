package waffle.guam.user.api

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import waffle.guam.user.infra.firebase.FirebaseClient.FirebaseTokenExpired
import waffle.guam.user.service.UserException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    fun dataIntegrityException(e: DataIntegrityViolationException) = ResponseEntity(e.message, HttpStatus.CONFLICT)

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun beanValidationException(e: MethodArgumentNotValidException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(value = [UserException::class])
    fun domainException(e: UserException) = ResponseEntity(e.msg, e.status)

    @ExceptionHandler(value = [FirebaseTokenExpired::class])
    fun authException(e: FirebaseTokenExpired) = ResponseEntity("", HttpStatus.UNAUTHORIZED)
}
