package io.wafflestudio.spring.slack

interface SlackClient {
    fun isEnabled()
    fun captureEvent(e: SlackEvent)
}
