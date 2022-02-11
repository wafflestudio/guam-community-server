package waffle.guam.immigration.server.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val id: Long = 0L,
    val firebaseUserId: String,
    val firebaseDeviceId: String? = null,
)
