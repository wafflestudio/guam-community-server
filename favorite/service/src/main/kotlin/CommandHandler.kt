package waffle.guam.favorite.service

interface CommandHandler<Command : Any, Result : Any> {
    suspend fun handle(command: Command): Result
}
