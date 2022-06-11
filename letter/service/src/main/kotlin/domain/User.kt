package waffle.guam.favorite.service.domain

data class User(
    val id: Long,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val nickname: String?,
    val email: String?,
    val profileImage: String?,
    val interests: List<Interest>,
) {
    val isProfileSet: Boolean
        get() {
            return !nickname.isNullOrBlank()
        }

    data class Interest(val name: String)
}
