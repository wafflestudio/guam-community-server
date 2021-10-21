package waffle.guam.community.service

open class GuamException(message: String) : Exception(message)

class InvalidArgumentException(message: String = "잘못된 인자입니다.") : GuamException(message)

class UserNotFound(message: String = "해당 유저를 찾을 수 없습니다.") : GuamException(message) {
    constructor(userId: Long) : this("해당 유저를 찾을 수 없습니다 [ID : $userId]")
}

class TagNotFound(message: String = "해당 태그를 찾을 수 없습니다.") : GuamException(message) {
    constructor(tagId: Long) : this("해당 태그를 찾을 수 없습니다 [ID : $tagId]")
}

class PostNotFound(message: String = "해당 게시물을 찾을 수 없습니다.") : GuamException(message) {
    constructor(postId: Long) : this("해당 게시물을 찾을 수 없습니다 [ID : $postId]")
}

class UnAuthorized(message: String = "허용되지 않은 요청입니다.") : GuamException(message)

class PostLikeConflict(message: String = "이미 좋아요를 누른 게시물입니다.") : GuamException(message) {
    constructor(postId: Long, userId: Long) : this("이미 좋아요를 누른 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}

class PostLikeNotFound(message: String = "좋아요를 누른 적이 없는 게시물입니다.") : GuamException(message) {
    constructor(postId: Long, userId: Long) : this("좋아요를 누른 적이 없는 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}
