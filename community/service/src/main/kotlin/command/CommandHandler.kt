package waffle.guam.community.service.command

interface CommandHandler<C : Command, R : Result> {
    fun handle(command: C): R
}
