package waffle.guam.community.config

import org.slf4j.MDC
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import waffle.guam.community.slack.SlackUtils
import java.util.UUID
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Profile("dev")
class RequestFilter(
    val slackUtils: SlackUtils
) : Filter {
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse?, filterChain: FilterChain) {
        val httpResponse = (servletResponse as? HttpServletResponse)
        if (httpResponse != null && 400 <= httpResponse.status) {
            val httpRequest = (servletRequest as? HttpServletRequest)
            val url = httpRequest?.requestURL
            // TODO 어떤 액션?
        }
        val uniqueId = UUID.randomUUID()
        MDC.put("request-id", uniqueId.toString())
        filterChain.doFilter(servletRequest, servletResponse)
    }
}
