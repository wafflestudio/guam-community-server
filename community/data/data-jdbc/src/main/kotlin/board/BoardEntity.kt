package waffle.guam.community.data.jdbc.board

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "boards")
@Entity
class BoardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val title: String
)

// 보드엔티티 아예 필요없을 것 같긴 한데 일단은
enum class BoardName(val idx: Long) {
    ANONYMOUS(1), FREE(2), CAREER(3), INFORMATION(4), AD(5);

    companion object {
        fun of(idx: Long): BoardName = values().find { it.idx == idx } ?: throw IllegalArgumentException("INVALID BOARD ID")
    }
}
