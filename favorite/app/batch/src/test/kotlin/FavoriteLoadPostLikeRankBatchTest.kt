package waffle.guam.favorite.batch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.jdbc.Sql
import waffle.guam.favorite.batch.job.FavoriteLoadPostLikeRankBatch
import waffle.guam.favorite.data.redis.RedisConfig

@BatchTest
@Sql("classpath:data/post.sql")
class FavoriteLoadPostLikeRankBatchTest @Autowired constructor(
    private val batchTestHelper: BatchTestHelper,
    private val redisTemplate: RedisTemplate<String, String>,
    private val community: BatchTest.TestCommunity,
) {
    @Test
    @DisplayName("좋아요 마이그레이션 테스트")
    fun postLikeLoadRank() {
        // given
        // postId: 1, count: 4
        // postId: 2, count: 1

        // when
        val batchResult = batchTestHelper.launch(FavoriteLoadPostLikeRankBatch.JOB_NAME)

        // then
        batchResult.shouldComplete()
        assertThat(redisTemplate.opsForZSet().size(RedisConfig.LIKE_KEY)).isEqualTo(2)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.LIKE_KEY, "1")).isEqualTo(4.0)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.LIKE_KEY, "2")).isEqualTo(1.0)
    }

    @Test
    @DisplayName("좋아요 게시판 별 마이그레이션 테스트")
    fun postLikeLoadRankPerBoardId() {
        // given
        // postId: 1, count: 4
        // postId: 2, count: 1
        community.setBoardIdOfPostForNextCall(
            1L to 5L,
            2L to 3L,
        )

        // when
        val batchResult = batchTestHelper.launch(FavoriteLoadPostLikeRankBatch.JOB_NAME)

        // then
        batchResult.shouldComplete()
        assertThat(redisTemplate.opsForZSet().size(RedisConfig.LIKE_KEY + "5")).isEqualTo(1)
        assertThat(redisTemplate.opsForZSet().size(RedisConfig.LIKE_KEY + "3")).isEqualTo(1)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.LIKE_KEY + "5", "1")).isEqualTo(4.0)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.LIKE_KEY + "3", "2")).isEqualTo(1.0)
    }
}
