package io.wafflestudio.spring.slack.samples

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExceptionController {

    @GetMapping("/test")
    suspend fun test() {
        error("This is test.")
    }
}
