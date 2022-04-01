package waffle.guam.community.data.jdbc.category

import waffle.guam.community.data.jdbc.post.PostEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "post_categories")
@Entity
data class PostCategoryEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val post: PostEntity,

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val category: CategoryEntity
)
