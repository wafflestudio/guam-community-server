package waffle.guam.community.common
import waffle.guam.community.service.GuamUnAuthorized

class MissingHeaderException(message: String = "헤더 정보를 찾을 수 없습니다.") :
    GuamUnAuthorized(message)