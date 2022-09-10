package waffle.guam.favorite.service

import java.time.Instant

interface Event {
    val eventTime: Instant
}
