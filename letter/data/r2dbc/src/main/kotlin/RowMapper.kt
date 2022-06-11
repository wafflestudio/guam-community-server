package waffle.guam.letter.data.r2dbc

import io.r2dbc.spi.Row
import java.time.LocalDateTime

fun Row.getString(name: String): String? = get(name, String::class.java)

fun Row.getLong(name: String): Long? = get(name, Number::class.java)?.toLong()

fun Row.getInt(name: String): Int? = get(name, Int::class.java)

fun Row.getBoolean(name: String): Boolean? = runCatching {
    get(name, Boolean::class.java)
}.getOrElse {
    // h2에서 변환이 안됨..
    get(name) as Boolean
}

fun Row.getLocalDateTime(name: String): LocalDateTime? = get(name, LocalDateTime::class.java)
