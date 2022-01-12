package waffle.guam.community.data.redis

import org.springframework.data.redis.core.StringRedisTemplate
import waffle.guam.community.data.GuamCache
import waffle.guam.community.data.GuamCacheFactory

class RedisGuamCacheFactory(
    private val redisTemplate: StringRedisTemplate,
) : GuamCacheFactory() {

    override fun <V : Any, K : Any> internalGetCache(property: Property<V, K>): GuamCache<V, K> =
        RedisGuamCacheImpl(
            name = property.name,
            ttl = property.ttl,
            loader = property.loader,
            multiLoader = property.multiLoader,
            redisTemplate = redisTemplate,
            type = property.type,
        )
}
