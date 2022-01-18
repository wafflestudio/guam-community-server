package waffle.guam.community.event

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import waffle.guam.community.service.command.Result

data class ExceptionOccurred(val exception: Throwable) :
    ResponseEntity<String>(exception.message, HttpStatus.INTERNAL_SERVER_ERROR), Result
