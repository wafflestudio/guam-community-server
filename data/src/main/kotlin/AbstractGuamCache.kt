package waffle.guam.community.data

import org.slf4j.LoggerFactory
import java.time.Duration

abstract class AbstractGuamCache<V : Any, K : Any>(
    open val name: String,
    open val ttl: Duration,
    private val loader: (K) -> V,
    private val multiLoader: ((Collection<K>) -> Map<K, V>)?,
) : GuamCache<V, K> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    abstract fun internalGet(key: K): V?
    abstract fun internalMultiGet(keys: Collection<K>): Map<K, V>
    abstract fun internalPut(key: K, value: V)
    abstract fun internalMultiPut(keyValue: Map<K, V>)
    abstract fun internalInvalidate(key: K)

    override fun get(key: K): V = internalGet(key) ?: loader.invoke(key).also { value -> internalPut(key, value) }

    override fun multiGet(keys: Collection<K>): Map<K, V> {
        val cached = internalMultiGet(keys)
        val missedKeys = keys - cached.keys

        val loaded =
            if (missedKeys.isEmpty()) {
                emptyMap()
            } else {
                multiLoader?.invoke(missedKeys) ?: missedKeys.associateWith { loader.invoke(it) }
            }

        if (loaded.isNotEmpty()) {
            internalMultiPut(loaded)
        }

        return cached + loaded
    }

    override fun reload(key: K) = internalPut(key, loader.invoke(key))

    override fun invalidate(key: K) = internalInvalidate(key)
}
