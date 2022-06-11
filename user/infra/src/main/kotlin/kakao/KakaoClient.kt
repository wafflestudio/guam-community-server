package waffle.guam.user.infra.kakao

import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody

interface KakaoClient {
    fun getUserId(kakaoToken: String): Long?
}

@Service
class KakaoClientImpl(
    webClientBuilder: WebClient.Builder,
) : KakaoClient {
    private val client = webClientBuilder.baseUrl("https://kapi.kakao.com/v2/user/me").build()

    override fun getUserId(kakaoToken: String): Long? = runBlocking {
        runCatching {
            client.get()
                .headers { it.set("Authorization", "Bearer $kakaoToken") }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<KaKaoRequestMeResponse>()
                .id
        }.recover {
            if (it is WebClientResponseException && it.statusCode == HttpStatus.NOT_FOUND) {
                null
            } else {
                throw it
            }
        }.getOrThrow()
    }

    private data class KaKaoRequestMeResponse(
        val id: Long,
    )
}
