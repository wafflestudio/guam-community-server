package waffle.guam.community.service.command

import org.springframework.stereotype.Service

@Service
class CommandDispatcher(
    private val allHandlers: List<CommandHandler<*, *>>
) {
    fun dispatch(command: Command) =
        allHandlers.firstOrNull { it.canHandle(command) }?.handle(command) ?: throw Exception()
}
