package waffle.guam.community.service.command

interface Result

// TODO
// interface PushEventResult : Result {
//    @get:JsonIgnore
//    val producedUserId: Long
//    @get:JsonIgnore
//    val consumingUserId: Long
//    @get:JsonIgnore
//    val needNotNotify: Boolean
//        get() = producedUserId == consumingUserId
//
//    fun toPushEventEntities(producedBy: Long): List<PushEventEntity>
// }
