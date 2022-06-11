package waffle.guam.favorite.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import waffle.guam.favorite.service.ServiceError

data class SuccessResponse<T>(
    val data: T,
)

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(value = [ServiceError::class])
    fun serviceError(e: ServiceError) = ResponseEntity(e.msg, HttpStatus.valueOf(e.status))
}
