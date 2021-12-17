package waffle.guam.community.controller.user

object UserUriConstants {
    private const val BASE_URI = "api/v1/users"

    const val USER_ME = "$BASE_URI/me"
    const val USER_DETAIL = "$BASE_URI/{userId}"
}
