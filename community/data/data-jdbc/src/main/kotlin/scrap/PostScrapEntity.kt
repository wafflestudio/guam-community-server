package waffle.guam.community.data.jdbc.scrap

import waffle.guam.community.data.jdbc.post.PostEntity
import waffle.guam.community.data.jdbc.user.UserEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Table(name = "post_scraps")
@Entity
class PostScrapEntity(
    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val post: PostEntity,

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val user: UserEntity
) {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id = 0L
}
