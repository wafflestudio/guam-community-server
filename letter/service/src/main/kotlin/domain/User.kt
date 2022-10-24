package waffle.guam.letter.service.domain

data class User(
    val id: Long,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val nickname: String?,
    val email: String?,
    val profileImage: String?,
    val interests: List<Interest>,
    val status: Status,
) {
    val isProfileSet: Boolean
        get() {
            return !nickname.isNullOrBlank()
        }

    data class Interest(val name: String)
    enum class Status { ACTIVE, INACTIVE }
}
