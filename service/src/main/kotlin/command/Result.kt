package waffle.guam.community.service.command

import com.fasterxml.jackson.annotation.JsonIgnore
import waffle.guam.community.data.jdbc.push.PushEventEntity
import waffle.guam.community.data.jdbc.user.UserEntity

interface Result

interface PushEventResult : Result {
    @get:JsonIgnore val producedUserId: Long
    @get:JsonIgnore val consumingUserId: Long
    @get:JsonIgnore val needNotNotify: Boolean
        get() = producedUserId == consumingUserId
    fun toPushEventEntities(producedBy: UserEntity): List<PushEventEntity>
}
