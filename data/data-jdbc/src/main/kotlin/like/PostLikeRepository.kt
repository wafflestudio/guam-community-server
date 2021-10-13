package waffle.guam.community.data.jdbc.like

import org.springframework.data.jpa.repository.JpaRepository

interface PostLikeRepository : JpaRepository<PostLikeEntity, Long> {
    fun findAllByPostId(postId: Long): List<PostLikeEntity>
    fun findAllByPostIdIn(postIds: Collection<Long>): List<PostLikeEntity>
}
