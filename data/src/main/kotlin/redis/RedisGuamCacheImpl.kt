package waffle.guam.community.data.redis

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import waffle.guam.community.data.AbstractGuamCache
import java.time.Duration
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

// TODO: change log level from info to debug
class RedisGuamCacheImpl<V : Any, K : Any> internal constructor(
    override val name: String,
    override val ttl: Duration,
    loader: (K) -> V,
    multiLoader: ((Collection<K>) -> Map<K, V>)? = null,
    private val redisTemplate: StringRedisTemplate,
    type: KType,
) : AbstractGuamCache<V, K>(name, ttl, loader, multiLoader) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
    private val valueType = mapper.typeFactory.constructType(type.javaType)

    override fun internalGet(key: K): V? {
        val value = redisTemplate.opsForValue().get(serializeKey(key))
            ?.let { deserializeValue(it) }

        return value.also { logger.debug("[Redis internalGet] key : {}, value : {}", key, value) }
    }

    override fun internalMultiGet(keys: Collection<K>): Map<K, V> {
        val values = redisTemplate.opsForValue().multiGet(serializeKeys(keys))
            ?.map { value -> value?.let { deserializeValue(it) } }
            ?: keys.map { null }

        return keys.zip(values)
            .mapNotNull { (k, v) -> v?.let { k to it } }
            .toMap()
            .also { values -> logger.debug("[Redis internalMultiGet] key : {}, value : {}", keys, values) }
    }

    override fun internalPut(key: K, value: V) {
        redisTemplate.opsForValue().set(serializeKey(key), serializeValue(value), ttl)
            .also { logger.debug("[Redis internalPut] key : {}, value: {}", key, value) }
    }

    override fun internalMultiPut(keyValue: Map<K, V>) {
        // multiSet can't set ttl
        keyValue.forEach { (k, v) -> internalPut(k, v) }
            .also { logger.debug("[Redis internalMultiPut] key: {}, value: {}", keyValue.keys, keyValue.values) }
    }

    override fun internalInvalidate(key: K) {
        redisTemplate.delete(serializeKey(key))
            .also { logger.debug("[Redis internalInvalidate] key: {}", key) }
    }

    private fun serializeKey(key: K) = "$name:${mapper.writeValueAsBytes(key).decodeToString()}"

    private fun serializeKeys(keys: Collection<K>) = keys.map(::serializeKey)

    private fun serializeValue(value: V) = value.let { mapper.writeValueAsString(it) }

    private fun deserializeValue(strValue: String): V = mapper.readValue(strValue, valueType)
}
