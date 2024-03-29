package waffle.guam.letter.service.query

import org.springframework.stereotype.Service
import waffle.guam.letter.data.r2dbc.data.isDeleted
import waffle.guam.letter.data.r2dbc.data.pairId
import waffle.guam.letter.data.r2dbc.repository.LetterBoxPreviewRepository
import waffle.guam.letter.data.r2dbc.repository.LetterBoxRepository
import waffle.guam.letter.service.domain.LetterBox
import waffle.guam.letter.service.domain.LetterBoxPreview
import waffle.guam.letter.service.domain.toDomain

interface LetterQueryService {
    suspend fun getLetterBoxPreviews(
        userId: Long,
        blockedUserIds: List<Long>,
    ): List<LetterBoxPreview>

    suspend fun getLetterBox(
        userId: Long,
        pairId: Long,
        size: Int = 50,
        letterIdSmallerThan: Long? = null,
    ): LetterBox?
}

@Service
class LetterQueryServiceImpl(
    private val letterBoxPreviewRepository: LetterBoxPreviewRepository,
    private val letterBoxRepository: LetterBoxRepository,
    private val userQueryService: UserQueryService,
) : LetterQueryService {

    override suspend fun getLetterBoxPreviews(userId: Long, blockedUserIds: List<Long>): List<LetterBoxPreview> {
        val entities = letterBoxPreviewRepository.findAll(userId = userId)
            .filterNot { it.isDeleted(userId) || it.pairId(userId) in blockedUserIds }

        val pairUsers = userQueryService.get(userIds = entities.map { it.pairId(userId) })

        return entities.map {
            LetterBoxPreview(
                userId = userId,
                pair = pairUsers[it.pairId(userId)]!!,
                lastLetter = it.lastLetterEntity.toDomain()
            )
        }
    }

    override suspend fun getLetterBox(userId: Long, pairId: Long, size: Int, letterIdSmallerThan: Long?): LetterBox? {
        return letterBoxRepository.find(
            userId = userId,
            pairId = pairId,
            size = size,
            letterIdSmallerThan = letterIdSmallerThan
        )?.let { lb ->
            LetterBox(
                id = lb.id,
                userId = userId,
                pair = userQueryService.get(pairId),
                letters = lb.letters!!.map { it.toDomain() }
            )
        }
    }
}
