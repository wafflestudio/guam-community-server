package waffle.guam.community.data.jdbc.common

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class ImagePathsConverter : AttributeConverter<List<String>, String> {

    override fun convertToDatabaseColumn(submitContents: List<String>?): String? =
        if (submitContents.isNullOrEmpty()) {
            null
        } else {
            submitContents.joinToString(",")
        }

    override fun convertToEntityAttribute(dbData: String?): List<String> =
        dbData?.split(",") ?: emptyList()
}

@Converter
class ImagePathConverter : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(submitContent: String?): String? =
        if (submitContent.isNullOrEmpty()) null
        else submitContent

    override fun convertToEntityAttribute(dbData: String?): String? = dbData
}
