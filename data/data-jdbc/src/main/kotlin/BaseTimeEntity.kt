package waffle.guam.community.data.jdbc

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    var createdAt: Instant = Instant.now()

    @LastModifiedDate
    var updatedAt: Instant = createdAt
}
