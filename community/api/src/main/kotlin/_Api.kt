package waffle.guam.community

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull
import kotlin.reflect.KClass

private val mapper: ObjectMapper =
    jacksonObjectMapper().registerModules(
        kotlinModule(),
        JavaTimeModule(),
    )

internal inline fun <reified T: Any> ServerRequest.query(pair: Pair<String, KClass<T>>): T {
    val queryParameter = this.queryParamOrNull(pair.first) ?: throw IllegalArgumentException("${pair.first} is required.")
    return mapper.readValue(queryParameter, pair.second.java)
}

internal inline fun <reified T : Any> ServerRequest.listQuery(pair: Pair<String, KClass<T>>): List<T> {
    val queryParameter = this.queryParamOrNull(pair.first) ?: throw IllegalArgumentException("${pair.first} is required.")
    return mapper.readValue(queryParameter, jacksonTypeRef<List<T>>())
}
