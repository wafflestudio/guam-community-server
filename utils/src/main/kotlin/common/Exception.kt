package waffle.guam.community.common

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

// HTTP 404
class UserNotFound(message: String = "해당 유저를 찾을 수 없습니다.") : GuamNotFound(message) {
    constructor(userId: Long) : this("해당 유저를 찾을 수 없습니다 [ID : $userId]")
    constructor(userIds: Collection<Long>) : this("해당 유저들을 찾을 수 없습니다 [ID : $userIds]")
}

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

// HTTP 409
class PostLikeConflict(message: String = "이미 좋아요를 누른 게시물입니다.") : GuamConflict(message) {
    constructor(postId: Long, userId: Long) : this("이미 좋아요를 누른 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}

class PostScrapConflict(message: String = "이미 스크랩한 게시물입니다.") : GuamConflict(message) {
    constructor(postId: Long, userId: Long) : this("이미 스크랩한 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}

class PostLikeNotFound(message: String = "좋아요를 누른 적이 없는 게시물입니다.") : GuamConflict(message) {
    constructor(postId: Long, userId: Long) : this("좋아요를 누른 적이 없는 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}

class PostScrapNotFound(message: String = "스크랩한 적이 없는 게시물입니다.") : GuamConflict(message) {
    constructor(postId: Long, userId: Long) : this("스크랩한 적이 없는 게시물 입니다. [USER_ID: $userId, POST_ID: $postId]")
}
