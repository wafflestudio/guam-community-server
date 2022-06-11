package waffle.guam.letter.data.r2dbc.data

data class LetterBoxPreviewEntity(
    val id: Long,
    val lowId: Long,
    val highId: Long,
    val lowDeleteMarkedId: Long?,
    val highDeleteMarkedId: Long?,
    val lastLetterEntity: LetterEntity,
)

fun LetterBoxPreviewEntity.pairId(userId: Long) = when (userId) {
    lowId -> highId
    highId -> lowId
    else -> throw RuntimeException("")
}
