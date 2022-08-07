package waffle.guam.favorite.batch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.jdbc.Sql
import waffle.guam.favorite.batch.job.FavoriteLoadScrapRankBatch
import waffle.guam.favorite.data.redis.RedisConfig

@BatchTest
@Sql("classpath:data/scrap.sql")
class FavoriteLoadPostScrapRankBatchTest @Autowired constructor(
    private val batchTestHelper: BatchTestHelper,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Test
    @DisplayName("게시글 스크랩 마이그레이션 테스트")
    fun postScrapLoadRank() {
        // given
        // postId: 1, count: 4
        // postId: 2, count: 1

        // when
        val batchResult = batchTestHelper.launch(FavoriteLoadScrapRankBatch.JOB_NAME)

        // then
        batchResult.shouldComplete()
        assertThat(redisTemplate.opsForZSet().size(RedisConfig.SCRAP_KEY)).isEqualTo(2)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.SCRAP_KEY, "1")).isEqualTo(4.0)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.SCRAP_KEY, "2")).isEqualTo(1.0)
    }
}
