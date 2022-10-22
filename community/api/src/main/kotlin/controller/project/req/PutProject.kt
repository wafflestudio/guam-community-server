package waffle.guam.community.controller.project.req

import waffle.guam.community.data.jdbc.project.Due

data class PutProject(
    val title: String,
    val description: String,
    val due: Due,
    val thumbnail: String,
    val techStacks: List<String>, // TODO ?
    val capacity: Capacity,
) {
    data class Capacity(
        val web: Int,
        val server: Int,
        val mobile: Int,
        val designer: Int,
    )
}
