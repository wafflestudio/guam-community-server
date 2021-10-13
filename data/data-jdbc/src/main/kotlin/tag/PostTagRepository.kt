package waffle.guam.community.data.jdbc.tag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PostTagRepository : JpaRepository<PostTagEntity, Long> {
    @Query("select pt from PostTagEntity pt inner join fetch pt.tag t where pt.post.id = :postId")
    fun findAllByPostId(postId: Long): List<PostTagEntity>
    @Query("select pt from PostTagEntity pt inner join fetch pt.tag t where pt.post.id in :postIds")
    fun findAllByPostIdIn(postIds: Collection<Long>): List<PostTagEntity>
}
