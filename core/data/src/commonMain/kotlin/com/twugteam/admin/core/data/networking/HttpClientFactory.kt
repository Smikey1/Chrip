package com.twugteam.admin.core.data.networking

import com.twugteam.admin.core.data.BuildKonfig
import com.twugteam.admin.core.data.auth.AuthInfoSerializable
import com.twugteam.admin.core.data.dto.RefreshRequest
import com.twugteam.admin.core.data.mapper.toDomain
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.logging.ChripLogger
import com.twugteam.admin.core.domain.utils.onFailure
import com.twugteam.admin.core.domain.utils.onSuccess
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val chripLogger: ChripLogger,
    private val sessionStorage: SessionStorage
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
            install(WebSockets) {
                pingIntervalMillis = 20_000L
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val authInfo = sessionStorage.observeAuthInfo().first()
                        authInfo?.let {
                            BearerTokens(
                                accessToken = it.accessToken,
                                refreshToken = it.refreshToken
                            )
                        }
                    }

                    refreshTokens {
                        if (response.request.url.encodedPath.contains("/auth")) {
                            return@refreshTokens null
                        }

                        val authInfo = sessionStorage.observeAuthInfo().firstOrNull()
                        if (authInfo?.refreshToken.isNullOrEmpty()) {
                            sessionStorage.setAuthInfo(null)
                            return@refreshTokens null
                        }
                        var bearerTokens: BearerTokens? = null
                        client.post<RefreshRequest, AuthInfoSerializable>(
                            route = "/auth/refresh",
                            body = RefreshRequest(
                                refreshToken = authInfo.refreshToken
                            ),
                            builder = {
                                markAsRefreshTokenRequest()
                            }
                        ).onSuccess { newAuthInfo ->
                            sessionStorage.setAuthInfo(newAuthInfo.toDomain())
                            bearerTokens = BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        }
                        .onFailure { _ ->
                            sessionStorage.setAuthInfo(null)
                        }
                        bearerTokens
                    }
                }
            }
        }
    }
}