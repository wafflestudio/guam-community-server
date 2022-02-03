package waffle.guam.community.controller.report.req

import waffle.guam.community.data.jdbc.report.ReportEntity

data class ReportRequest(
    val suspectId: Long,
    val letterBoxId: Long,
    val type: ReportEntity.Type,
)
