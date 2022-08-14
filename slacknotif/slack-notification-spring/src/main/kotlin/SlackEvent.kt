package io.wafflestudio.spring.slack

import org.springframework.web.server.ServerWebExchange
import javax.servlet.http.HttpServletRequest

class SlackEvent {
    // uri
    constructor(exchange: ServerWebExchange, ex: Throwable)
    constructor(request: HttpServletRequest, ex: Exception)
}
