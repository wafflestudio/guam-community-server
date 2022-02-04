package waffle.guam.community.data.jdbc.report

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
/**
 * Details TBD
 */
class ReportEntity(
    val reporterId: Long,
    val suspectId: Long,
    val reportType: Type,
) {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L

    enum class Type(description: String) {
        ABUSE("욕설/비방"),
        OBSCENITY("음란"),
        PIRACY("불법 복제/무단 도용"),
        GAMBLING("사행성 홍보"),
        SPAMMING("도배"),
    }
}
