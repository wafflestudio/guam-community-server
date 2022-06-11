package waffle.guam.community.service

abstract class GuamException(message: String, val status: Int) : RuntimeException(message)

open class GuamBadRequest(message: String) : GuamException(message, 400)

open class GuamUnAuthorized(message: String) : GuamException(message, 401)

open class GuamForbidden(message: String) : GuamException(message, 403)

open class GuamNotFound(message: String) : GuamException(message, 404)

open class GuamConflict(message: String) : GuamException(message, 409)

// HTTP 400
class InvalidArgumentException(message: String = "잘못된 인자입니다.") :
    GuamBadRequest(message)

class BadBoardId(message: String = "잘못된 게시판입니다.") : GuamBadRequest(message) {
    constructor(boardId: Long) : this("잘못된 게시판 ID 입니다. [ID : $boardId]")
}

class BadCategoryId(message: String = "잘못된 카테고리입니다.") : GuamBadRequest(message) {
    constructor(categoryId: Long) : this("잘못된 카테고리 ID 입니다. [ID : $categoryId]")
}

// HTTP 403
class Forbidden(message: String = "허용되지 않은 요청입니다.") :
    GuamForbidden(message)

class LetterNotFound(message: String = "해당 쪽지를 찾을 수 없습니다.") : GuamNotFound(message) {
    constructor(letterId: Long) : this("해당 쪽지를 찾을 수 없습니다 [ID : $letterId]")
}

class PostNotFound(message: String = "해당 게시물을 찾을 수 없습니다.") : GuamNotFound(message) {
    constructor(postId: Long) : this("해당 게시물을 찾을 수 없습니다 [ID : $postId]")
    constructor(postIds: Collection<Long>) : this("해당 게시물 목록을 찾을 수 없습니다 [ID : $postIds]")
}

class PostCommentNotFound(message: String = "해당 댓글을 찾을 수 없습니다.") : GuamNotFound(message) {
    constructor(commentId: Long) : this("해당 댓글을 찾을 수 없습니다 [ID : $commentId]")
}
