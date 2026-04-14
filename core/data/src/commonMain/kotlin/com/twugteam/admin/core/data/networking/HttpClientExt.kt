package com.twugteam.admin.core.data.networking

import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse


expect suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote>


suspend inline fun <reified T> safeCall(noinline execute: suspend () -> HttpResponse): Result<T, DataError.Remote> {
    return platformSafeCall(
        execute = execute,
        handleResponse = { response ->
            responseToResult(response)
        }
    )
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                Result.Success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Result.Failure(DataError.Remote.SERIALIZATION)
            }
        }

        400 -> Result.Failure(DataError.Remote.BAD_REQUEST)
        401 -> Result.Failure(DataError.Remote.UNAUTHORIZED)
        403 -> Result.Failure(DataError.Remote.FORBIDDEN)
        404 -> Result.Failure(DataError.Remote.NOT_FOUND)
        408 -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
        409 -> Result.Failure(DataError.Remote.CONFLICT)
        413 -> Result.Failure(DataError.Remote.PAYLOAD_TOO_LARGE)
        429 -> Result.Failure(DataError.Remote.TOO_MANY_REQUESTS)
        500 -> Result.Failure(DataError.Remote.SERVER_ERROR)
        503 -> Result.Failure(DataError.Remote.SERVICE_UNAVAILABLE)
        else -> Result.Failure(DataError.Remote.UNKNOWN)
    }
}

fun constructRoute(route: String): String {
    return when {
        route.contains(UrlConstraints.BASE_URL) -> route
        route.startsWith("/") -> UrlConstraints.BASE_URL + route
        else -> "${UrlConstraints.BASE_URL}/$route"
    }
}

suspend fun HttpClient.get(
    route: String,
    block: HttpRequestBuilder.() -> Unit = {}
) {
    get(
        urlString = constructRoute(route),
        block = block
    )
}