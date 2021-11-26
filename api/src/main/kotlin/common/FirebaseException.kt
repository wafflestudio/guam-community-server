package waffle.guam.community.common
import waffle.guam.community.service.GuamUnAuthorized

class InvalidFirebaseTokenException(message: String = "잘못된 토큰입니다.") :
    GuamUnAuthorized(message)
