package io.wafflestudio.spring.slack.webmvc

import io.wafflestudio.spring.slack.SlackClient
import io.wafflestudio.spring.slack.SlackEvent
import org.springframework.core.annotation.Order
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Order(-1)
class SlackExceptionResolver(
    private val slackClient: SlackClient,
) : HandlerExceptionResolver {

    override fun resolveException(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any?,
        ex: Exception,
    ): ModelAndView? {
        val event = SlackEvent(request, ex)

        slackClient.captureEvent(event)

        return null
    }
}
