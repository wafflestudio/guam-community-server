package waffle.guam.letter.service

abstract class ServiceError : RuntimeException() {
    abstract val status: Int
    abstract val msg: String
}
