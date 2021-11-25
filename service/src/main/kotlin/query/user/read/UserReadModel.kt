package waffle.guam.community.service.query.user.read

import org.springframework.stereotype.Service
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.user.UserCollector

// TODO 네이밍 재고
@Service
class UserReadModel(
    private val userCollector: UserCollector
) {
    fun getUser(userId: Long): User =
        userCollector.get(userId)
}
