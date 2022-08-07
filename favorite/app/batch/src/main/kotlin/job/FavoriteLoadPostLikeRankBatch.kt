package waffle.guam.favorite.batch.job

import org.springframework.stereotype.Component
import waffle.guam.favorite.batch.job.BatchJobNames.LOAD_POST_LIKE_RANK
import waffle.guam.favorite.batch.service.PostLikeBatchService
import waffle.guam.favorite.data.r2dbc.PostLikeCount

@Component(LOAD_POST_LIKE_RANK)
class FavoriteLoadPostLikeRankBatch(
    private val postLikeBatchService: PostLikeBatchService,
) : BatchJob<PostLikeCount>() {

    override fun initStep() {
        postLikeBatchService.clearRank()
    }

    override fun doRead(page: Int, pageSize: Int): List<PostLikeCount> {
        return postLikeBatchService.fetchCounts(page, pageSize)
    }

    override fun List<PostLikeCount>.writeToRedis() {
        postLikeBatchService.loadRank(this)
    }
}
