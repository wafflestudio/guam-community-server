package waffle.guam.community.data

import java.time.Duration
import kotlin.reflect.KType
import kotlin.reflect.typeOf

abstract class GuamCacheFactory {

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <K : Any, reified V : Any> getCache(
        name: String,
        ttl: Duration = Duration.ofMinutes(1),
        noinline loader: (K) -> V,
        noinline multiLoader: ((Collection<K>) -> Map<K, V>)? = null,
    ): GuamCache<V, K> =
        internalGetCache(Property(name, ttl, loader, multiLoader, typeOf<V>()))

    abstract fun <V : Any, K : Any> internalGetCache(property: Property<V, K>): GuamCache<V, K>

    class Property<V, K>(
        val name: String,
        val ttl: Duration,
        val loader: (K) -> V,
        val multiLoader: ((Collection<K>) -> Map<K, V>)?,
        val type: KType
    )
}
