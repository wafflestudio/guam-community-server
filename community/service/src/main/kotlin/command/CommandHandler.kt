package waffle.guam.community.service.command

interface CommandHandler<C : Command, R : EventResult> {
    fun handle(command: C): R
}
