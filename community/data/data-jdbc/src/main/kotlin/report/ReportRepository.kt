package waffle.guam.community.data.jdbc.report

import org.springframework.data.jpa.repository.JpaRepository

interface ReportRepository : JpaRepository<ReportEntity, Long>
