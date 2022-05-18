package waffle.guam.community.service.query.push

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.service.domain.push.PushEvent
import waffle.guam.community.service.domain.push.PushEventList
import waffle.guam.community.service.query.Collector
import waffle.guam.community.service.query.push.PushEventListCollector.Query

@Service
class PushEventListCollector(
    private val pushEventRepository: PushEventRepository,
) : Collector<PushEventList, Query> {

    override fun get(query: Query): PushEventList = pushEventRepository.findAllByUserIdOrderByIdDesc(
        query.userId, PageRequest.of(query.page, query.size)
    )
        .map(::PushEvent)
        .let { PushEventList(userId = query.userId, content = it.content, hasNext = it.hasNext()) }

    data class Query(
        val userId: Long,
        val page: Int,
        val size: Int,
    )
}
