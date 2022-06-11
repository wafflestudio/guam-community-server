package waffle.guam.favorite.service.command

interface CommandHandler<Command : Any, Result : Any> {
    suspend fun handle(command: Command): Result
}
