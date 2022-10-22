package waffle.guam.community.controller.project.req

import waffle.guam.community.data.jdbc.project.Due

data class GetProjectList(
    val almostFull: Boolean,
    val keyword: String,
    val skill: String,
    val due: Due,
    val position: String, // TODO ?
)
