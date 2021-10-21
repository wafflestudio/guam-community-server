package waffle.guam.community.service

open class GuamException(message: String) : Exception(message)

class InvalidArgumentException(message: String = "잘못된 인자입니다.") : GuamException(message)

class UserNotFound(message: String = "해당 유저를 찾을 수 없습니다.") : GuamException(message) {
    constructor(userId: Long) : this("해당 유저를 찾을 수 없습니다 [ID : $userId]")
}

class TagNotFound(message: String = "해당 태그를 찾을 수 없습니다.") : GuamException(message) {
    constructor(tagId: Long) : this("해당 태그를 찾을 수 없습니다 [ID : $tagId]")
}

class PostNotFound(message: String = "해당 포스트를 찾을 수 없습니다.") : GuamException(message) {
    constructor(postId: Long) : this("해당 포스트를 찾을 수 없습니다 [ID : $postId]")
}

class UnAuthorized(message: String = "허용되지 않은 요청입니다.") : GuamException(message)
