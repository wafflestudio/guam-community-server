package waffle.guam.community.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.tag.TagEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import waffle.guam.community.service.GuamException
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class ErrorHandler {
    private val logger = LoggerFactory.getLogger(this::javaClass.name)

    @ExceptionHandler(value = [EntityNotFoundException::class])
    fun entityNotFound(e: EntityNotFoundException): ResponseEntity<Any> {
        val matcher = "Unable to find (.+) with id \\d".toRegex()
        val verboseName = captureEntityVerboseName(e.message, matcher)
        val resultMessage = verboseName
            ?.run { "$this 정보를 찾을 수 없습니다." }
            ?: e.message

        return ResponseEntity(resultMessage, HttpStatus.NOT_FOUND)
    }

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

    private fun captureEntityVerboseName(errorMessage: String?, matcher: Regex): String? {
        val message = errorMessage ?: ""
        val capturedEntityClassName = matcher.find(message)
            ?.destructured
            ?.toList()
            ?.lastOrNull()

        return EntityNameMapper.get(capturedEntityClassName)
    }
}

private object EntityNameMapper {

    private val entityNameMap = mapOf(
        PostEntity::class.qualifiedName to "게시물",
        TagEntity::class.qualifiedName to "태그",
        UserEntity::class.qualifiedName to "유저",
    )

    fun get(className: String?) = entityNameMap[className]
}
