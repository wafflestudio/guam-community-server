package waffle.guam.favorite.service

abstract class ServiceError : RuntimeException() {
    abstract val status: Int
    abstract val msg: String
}
