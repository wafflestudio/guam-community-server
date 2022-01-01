package waffle.guam.community.data

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.StringRedisTemplate
import waffle.guam.community.data.caffeine.CaffeineGuamCacheFactory
import waffle.guam.community.data.redis.RedisGuamCacheFactory

@Configuration
class GuamCacheConfiguration {

    // default caffeine, maximumSize = 1000
    @Primary
    @Bean("caffeine")
    fun caffeineGuamCacheFactory(): GuamCacheFactory =
        CaffeineGuamCacheFactory(maximumSize = 1000)

    @Bean("redis")
    fun redisGuamCacheFactory(redisTemplate: StringRedisTemplate): GuamCacheFactory =
        RedisGuamCacheFactory(redisTemplate)
}
