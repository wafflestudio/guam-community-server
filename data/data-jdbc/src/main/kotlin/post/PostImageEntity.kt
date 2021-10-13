package waffle.guam.community.data.jdbc.post

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "post_images")
@Entity
class PostImageEntity(
    @GeneratedValue
    @Id
    val id: Long,
    val postId: Long,
    val imageUrl: String
)
