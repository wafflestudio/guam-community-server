package waffle.guam.favorite.batch

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.jdbc.Sql
import waffle.guam.favorite.batch.job.FavoriteLoadCommentLikeRankBatch
import waffle.guam.favorite.data.redis.RedisConfig

@BatchTest
@Sql("classpath:data/test.sql")
class FavoriteLoadPostCommentLikeRankBatchTest @Autowired constructor(
    private val batchTestHelper: BatchTestHelper,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    @Test
    @DisplayName("댓글 좋아요 마이그레이션 테스트")
    fun postCommentLikeLoadRank() {
        // given
        // postId: 10, count: 3
        // postId: 20, count: 2

        // when
        val batchResult = batchTestHelper.launch(FavoriteLoadCommentLikeRankBatch.JOB_NAME)

        // then
        batchResult.shouldComplete()
        assertThat(redisTemplate.opsForZSet().size(RedisConfig.COMMENT_LIKE_KEY)).isEqualTo(2)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.COMMENT_LIKE_KEY, "10")).isEqualTo(3.0)
        assertThat(redisTemplate.opsForZSet().score(RedisConfig.COMMENT_LIKE_KEY, "20")).isEqualTo(2.0)
    }
}
