package waffle.guam.community.data.caffeine

import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import waffle.guam.community.data.AbstractGuamCache
import java.time.Duration

// TODO: change log level from info to debug
class CaffeineGuamCacheImpl<V : Any, K : Any> internal constructor(
    override val name: String,
    override val ttl: Duration,
    loader: (K) -> V,
    multiLoader: ((Collection<K>) -> Map<K, V>)? = null,
    maximumSize: Long,
) : AbstractGuamCache<V, K>(name, ttl, loader, multiLoader) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val caffeineCache = Caffeine.newBuilder()
        .maximumSize(maximumSize)
        .expireAfterWrite(ttl)
        .build<K, V>()

    override fun internalGet(key: K): V? =
        caffeineCache.getIfPresent(key)
            .also { value -> logger.debug("[Caffeine internalGet] key : {}, value : {}", key, value) }

    override fun internalMultiGet(keys: Collection<K>): Map<K, V> =
        caffeineCache.getAllPresent(keys)
            .also { values -> logger.debug("[Caffeine internalMultiGet] key : {}, value : {}", keys, values) }

    override fun internalPut(key: K, value: V): Unit =
        caffeineCache.put(key, value)
            .also { logger.debug("[Caffeine internalPut] key : {}, value: {}", key, value) }

    override fun internalMultiPut(keyValue: Map<K, V>): Unit =
        caffeineCache.putAll(keyValue)
            .also { logger.debug("[Caffeine internalMultiPut] key: {}, value: {}", keyValue.keys, keyValue.values) }

    override fun internalInvalidate(key: K): Unit =
        caffeineCache.invalidate(key)
            .also { logger.debug("[Caffeine internalInvalidate] key: {}", key) }
}
