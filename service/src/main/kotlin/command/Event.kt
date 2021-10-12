package waffle.guam.community.service.command

sealed class Event<out V, out E> {
    data class Result<out V>(val value: V) : Event<V, Nothing>()
    data class Error<out E>(val error: E) : Event<Nothing, E>()
}
