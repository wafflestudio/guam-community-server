package waffle.guam.community.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GuamExceptionHandler {

    @ExceptionHandler(value = [Exception::class])
    fun internalError(e: Exception) =
        ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
}
