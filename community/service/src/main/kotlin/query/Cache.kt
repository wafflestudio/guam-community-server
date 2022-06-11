package waffle.guam.community.service.query

import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import java.time.Duration

class Cache<V, K>(
    maximumSize: Long,
    duration: Duration,
    private val loader: (K) -> V,
    private val multiLoader: ((Collection<K>) -> Map<K, V>)? = null,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumSize)
        .expireAfterWrite(duration)
        .build<K, V>()

    fun get(key: K): V =
        caffeineCache.getIfPresent(key) ?: loader.invoke(key).also { value -> caffeineCache.put(key, value) }

    fun multiGet(keys: Collection<K>): Map<K, V> {
        val cached = caffeineCache.getAllPresent(keys)
        val missedKeys = keys - cached.keys

        val loaded =
            if (missedKeys.isEmpty()) {
                emptyMap()
            } else {
                multiLoader?.invoke(missedKeys) ?: missedKeys.map { it to loader.invoke(it) }.toMap()
            }

        if (loaded.isNotEmpty()) {
            caffeineCache.putAll(loaded)
        }

        return cached + loaded
    }

    fun reload(key: K) = caffeineCache.put(key, loader.invoke(key))

    fun invalidate(key: K) = caffeineCache.invalidate(key)
}
