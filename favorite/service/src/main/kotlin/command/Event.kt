package waffle.guam.favorite.service.command

import java.time.Instant

interface Event {
    val eventTime: Instant
}
