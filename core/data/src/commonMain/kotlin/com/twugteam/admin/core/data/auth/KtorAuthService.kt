package com.twugteam.admin.core.data.auth

import com.twugteam.admin.core.data.dto.RegisterRequest
import com.twugteam.admin.core.data.dto.ResendEmailVerificationRequest
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.data.networking.post
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.asEmptyDataResult
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {

    companion object {
        private const val REGISTER_ENDPOINT = "/auth/register"
        private const val RESEND_VERIFICATION_ENDPOINT = "/auth/resend-verification"
        private const val VERIFY_EMAIL_ENDPOINT = "/auth/verify"
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post<RegisterRequest, Unit>(
            route = REGISTER_ENDPOINT,
            body = RegisterRequest(
                username = username,
                email = email,
                password = password
            )
        ).asEmptyDataResult()
    }

    override suspend fun resendVerificationEmail(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ResendEmailVerificationRequest, Unit>(
            route = RESEND_VERIFICATION_ENDPOINT,
            body = ResendEmailVerificationRequest(
                email = email
            )
        ).asEmptyDataResult()
    }

    override suspend fun verifyEmail(token: String): EmptyResult<DataError.Remote> {
        return httpClient.get(
            route = VERIFY_EMAIL_ENDPOINT,
            queryParams = mapOf(
                "token" to token
            )
        )
    }

}