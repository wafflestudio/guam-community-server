package waffle.guam.favorite.data.redis.repository

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

internal suspend fun ReactiveStringRedisTemplate.zGet(key: String, id: Long) =
    opsForZSet().score("$key", "$id").awaitFirstOrNull()?.toInt() ?: 0

internal suspend fun ReactiveStringRedisTemplate.zGets(key: String, ids: List<Long>): Map<Long, Int> {
    if (ids.isEmpty()) {
        return emptyMap()
    }

    val targets = ids.map { "$it" }.toTypedArray()

    val scores = opsForZSet().score(key, *targets)
        .awaitSingle()

    return ids.zip(scores).associate { it.first to (it.second?.toInt() ?: 0) }
}

internal suspend fun ReactiveStringRedisTemplate.zAdd(key: String, id: Long, value: Int) =
    opsForZSet().add(key, "$id", value.toDouble())
        .awaitSingle()

internal suspend fun ReactiveStringRedisTemplate.zInc(key: String, id: Long, delta: Double): Int =
    opsForZSet().incrementScore("$key", "$id", delta)
        .awaitSingle()
        .toInt()
