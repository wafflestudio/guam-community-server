package waffle.guam.community.data.jdbc.report

import org.springframework.data.jpa.repository.JpaRepository
import waffle.guam.community.data.jdbc.BaseTimeEntity
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

interface ReportRepository: JpaRepository<ReportEntity, Long>
