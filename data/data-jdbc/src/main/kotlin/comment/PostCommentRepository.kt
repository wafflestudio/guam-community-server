package waffle.guam.community.data.jdbc.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostCommentRepository : JpaRepository<PostCommentEntity, Long> {
    @Query("select pc from PostCommentEntity pc inner join fetch pc.user u where pc.post.id = :postId")
    fun findAllByPostId(postId: Long): List<PostCommentEntity>
    @Query("select pc from PostCommentEntity pc inner join fetch pc.user u where pc.post.id in :postIds")
    fun findAllByPostIdIn(postIds: Collection<Long>): List<PostCommentEntity>
}
