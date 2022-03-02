package waffle.guam.community.service.domain.image

enum class ImageStorage {
    LOCAL, DEV, PROD;

    companion object {
        fun from(env: String): ImageStorage? =
            kotlin.runCatching { valueOf(env.uppercase()) }.getOrNull()
    }
}
