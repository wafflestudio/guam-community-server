package waffle.guam.community.service.domain.push

data class PushEventList(
    val userId: Long,
    val content: List<PushEvent>,
    val hasNext: Boolean,
)
