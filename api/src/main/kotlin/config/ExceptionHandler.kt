package waffle.guam.community.config

import GuamException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandler {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    @ExceptionHandler(value = [HttpRequestMethodNotSupportedException::class])
    fun methodNotAllowed(e: HttpRequestMethodNotSupportedException) =
        ResponseEntity(e.message, HttpStatus.METHOD_NOT_ALLOWED)

    @ExceptionHandler(value = [GuamException::class])
    fun guamError(e: GuamException) =
        ResponseEntity(e.message, e.status)

    @ExceptionHandler(value = [Exception::class])
    fun internalError(e: Exception) =
        ResponseEntity("알 수 없는 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR).also {
            logger.error(e.message, e)
        }
}
