package waffle.guam.community.data.jdbc.user

import waffle.guam.community.data.jdbc.common.ImagePathConverter
import waffle.guam.community.data.jdbc.stack.StackEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Table(name = "users")
@Entity
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val firebaseUid: String,

    val email: String? = null,

    @Column(length = 10)
    // TODO Null인 경우 API 사용 불가
    var nickname: String? = null,

    @Column(length = 200)
    var introduction: String? = null,

    var githubId: String? = null,

    var blogUrl: String? = null,

    @Convert(converter = ImagePathConverter::class)
    var profileImage: String? = null,

    @OneToMany(mappedBy = "data.userId", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val stacks: MutableList<StackEntity> = mutableListOf(),
)
