package waffle.guam.letter.service.query

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import waffle.guam.letter.service.domain.Block

interface BlockQueryService {
    fun getBlockList(userId: Long): Mono<Block>
}

@Service
@EnableConfigurationProperties(UserServiceProperties::class)
class BlockQueryServiceImpl(
    webClientBuilder: WebClient.Builder,
    userServiceProperties: UserServiceProperties,
) : BlockQueryService {
    private val webClient = webClientBuilder.baseUrl(userServiceProperties.baseUrl).build()

    override fun getBlockList(userId: Long): Mono<Block> {
        return webClient.get()
            .uri("/api/v1/blocks")
            .header("X-GATEWAY-USER-ID", userId.toString())
            .retrieve()
            .bodyToMono()
    }
}
