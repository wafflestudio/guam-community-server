package waffle.guam.immigration.server.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val id: Long,
    val firebaseId: String,
    val deviceId: String?,
)
