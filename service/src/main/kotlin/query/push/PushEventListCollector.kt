package waffle.guam.community.service.query.push

import org.springframework.stereotype.Service
import waffle.guam.community.data.jdbc.push.PushEventRepository
import waffle.guam.community.service.UserId
import waffle.guam.community.service.domain.push.PushEvent
import waffle.guam.community.service.domain.push.PushEventList
import waffle.guam.community.service.query.Collector

@Service
class PushEventListCollector(
    private val pushEventRepository: PushEventRepository,
) : Collector<PushEventList, UserId> {

    override fun get(id: UserId): PushEventList = pushEventRepository.findAllByUserId(id)
        .map(::PushEvent)
        .let { PushEventList(userId = id, it) }
}
