package waffle.guam.community.data.caffeine

import waffle.guam.community.data.GuamCache
import waffle.guam.community.data.GuamCacheFactory

class CaffeineGuamCacheFactory(
    private val maximumSize: Long = 1000,
) : GuamCacheFactory() {

    override fun <V : Any, K : Any> internalGetCache(
        property: Property<V, K>,
    ): GuamCache<V, K> =
        CaffeineGuamCacheImpl(
            name = property.name,
            ttl = property.ttl,
            loader = property.loader,
            multiLoader = property.multiLoader,
            maximumSize = maximumSize
        )
}
