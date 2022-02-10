package waffle.guam.immigration.app.config

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import waffle.guam.immigration.server.exception.ImmigrationException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ImmigrationException::class)
    fun unAuthorized(exc: ImmigrationException) =
        ResponseEntity(exc.message, exc.status)
}
