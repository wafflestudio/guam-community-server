package waffle.guam.community.service.query.user.displayer

import org.springframework.stereotype.Service
import waffle.guam.community.service.domain.user.User
import waffle.guam.community.service.query.user.UserCollector

@Service
class UserDisplayer(
    private val userCollector: UserCollector
) {
    fun getUser(userId: Long): User =
        userCollector.get(userId)
}
