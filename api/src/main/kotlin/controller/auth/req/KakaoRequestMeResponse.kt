package waffle.guam.community.controller.auth.req

data class KaKaoRequestMeResponse(
    val connected_at: String?,
    val id: Int,
    val kakao_account: KakaoAccount,
    val properties: Properties?,
)

data class KakaoAccount(
    val age_range: String?,
    val age_range_needs_agreement: Boolean?,
    val birthday: String?,
    val birthday_needs_agreement: Boolean?,
    val birthday_type: String?,
    val email: String?,
    val email_needs_agreement: Boolean?,
    val gender_needs_agreement: Boolean?,
    val has_age_range: Boolean,
    val has_birthday: Boolean,
    val has_email: Boolean,
    val has_gender: Boolean,
    val is_email_valid: Boolean,
    val is_email_verified: Boolean,
    val profile: Profile?,
    val profile_needs_agreement: Boolean?,
)

data class Profile(
    val is_default_image: Boolean,
    val nickname: String,
    val profile_image_url: String,
    val thumbnail_image_url: String,
)

data class Properties(
    val nickname: String,
    val profile_image: String,
    val thumbnail_image: String,
)
