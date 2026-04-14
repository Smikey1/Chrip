package com.twugteam.admin.core.data.networking

import com.twugteam.admin.core.data.BuildKonfig
import com.twugteam.admin.core.domain.logging.ChripLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private var chripLogger: ChripLogger
) {
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            defaultRequest {
                header("x-api-key", BuildKonfig.API_KEY)
                contentType(ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                connectTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        chripLogger.debug(message)
                    }
                }
            }
            install(WebSockets){
                pingIntervalMillis = 20_000L
            }
        }
    }
}