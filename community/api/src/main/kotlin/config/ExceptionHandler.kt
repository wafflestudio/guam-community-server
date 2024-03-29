package waffle.guam.community.config

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import waffle.guam.community.service.GuamException
import waffle.guam.community.service.Log
import javax.persistence.EntityExistsException

@ControllerAdvice
class ExceptionHandler {
    companion object : Log

    @ExceptionHandler(value = [MaxUploadSizeExceededException::class])
    fun fileSize(e: MaxUploadSizeExceededException) =
        ResponseEntity("파일 사이즈가 너무 큽니다.", HttpStatus.PAYLOAD_TOO_LARGE)

    @ExceptionHandler(value = [EntityExistsException::class, DataIntegrityViolationException::class])
    fun entityExists(e: Exception) =
        ResponseEntity("이미 존재하는 값입니다.", HttpStatus.CONFLICT)

    @ExceptionHandler(value = [EmptyResultDataAccessException::class])
    fun emptyResults(e: EmptyResultDataAccessException) =
        ResponseEntity("존재하지 않는 값입니다.", HttpStatus.NOT_FOUND)

    @ExceptionHandler(value = [GuamException::class])
    fun guamError(e: GuamException) =
        ResponseEntity(e.message, HttpStatus.valueOf(e.status))
}
