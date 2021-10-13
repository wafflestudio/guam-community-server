package waffle.guam.community.data.jdbc.post

import waffle.guam.community.data.jdbc.tag.PostTagEntity
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(name = "posts")
@Entity
class PostEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,
    val boardId: Long,
    val userId: Long,
    val title: String,
    val content: String,
    @Enumerated(value = EnumType.STRING)
    var status: Status = Status.VALID,
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val tags: MutableSet<PostTagEntity> = mutableSetOf(),
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = createdAt,
) {
    enum class Status {
        VALID, DELETED
    }
}
