package waffle.guam.user.infra.db

import javax.persistence.AttributeConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Converter
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "users")
@Entity
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0L,

    val email: String? = null,

    @Column(length = 10, unique = true)
    var nickname: String = "",

    @Column(length = 200)
    var introduction: String? = null,

    var githubId: String? = null,

    var blogUrl: String? = null,

    var profileImage: String? = null,

    @Convert(converter = InterestsConverter::class)
    val interests: MutableList<String> = mutableListOf(),

    @Column(unique = true)
    val firebaseId: String? = null,
)

@Converter
class InterestsConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>?): String? {
        return if (attribute.isNullOrEmpty()) {
            null
        } else {
            attribute.joinToString(",")
        }
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<String> {
        return dbData?.split(",")?.toMutableList() ?: mutableListOf()
    }
}
