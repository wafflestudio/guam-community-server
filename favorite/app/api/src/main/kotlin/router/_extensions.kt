package waffle.guam.favorite.api.router

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun ServerRequest.getHeader(headerName: String): String =
    getHeaderOrNull(headerName).let(::requireNotNull)

fun ServerRequest.getHeaderOrNull(headerName: String): String? =
    headers().firstHeader(headerName)

fun ServerRequest.getParam(param: String) =
    queryParamOrNull(param).let(::requireNotNull)
