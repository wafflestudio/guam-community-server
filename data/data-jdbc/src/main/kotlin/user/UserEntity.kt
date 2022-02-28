package waffle.guam.community.data.jdbc.user

import waffle.guam.community.data.jdbc.common.ImagePathConverter
import waffle.guam.community.data.jdbc.interest.InterestEntity
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    val id: Long, // same as immigration service ID

    val email: String? = null,

    @Column(length = 10, unique = true)
    var nickname: String? = null,

    @Column(length = 200)
    var introduction: String? = null,

    var githubId: String? = null,

    var blogUrl: String? = null,

    @Convert(converter = ImagePathConverter::class)
    var profileImage: String? = null,

    @OneToMany(mappedBy = "data.userId", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val interests: MutableList<InterestEntity> = mutableListOf(),
)
