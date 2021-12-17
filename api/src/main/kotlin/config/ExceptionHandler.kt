package waffle.guam.community.config

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import waffle.guam.community.service.GuamException

@ControllerAdvice
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    @ExceptionHandler(value = [GuamException::class])
    fun guamError(e: GuamException) =
        ResponseEntity(e.message, e.status)
}
