package com.twugteam.admin.core.data.auth

import com.twugteam.admin.core.data.dto.ChangePasswordRequest
import com.twugteam.admin.core.data.dto.ForgotPasswordRequest
import com.twugteam.admin.core.data.dto.LoginRequest
import com.twugteam.admin.core.data.dto.RegisterRequest
import com.twugteam.admin.core.data.dto.ResendEmailVerificationRequest
import com.twugteam.admin.core.data.dto.ResetPasswordRequest
import com.twugteam.admin.core.data.mapper.toDomain
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.data.networking.post
import com.twugteam.admin.core.domain.auth.AuthInfo
import com.twugteam.admin.core.domain.auth.AuthService
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.asEmptyDataResult
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient

class KtorAuthService(
    private val httpClient: HttpClient
) : AuthService {

    companion object {
        private const val REGISTER_ENDPOINT = "/auth/register"
        private const val LOGIN_ENDPOINT = "/auth/login"
        private const val RESEND_VERIFICATION_ENDPOINT = "/auth/resend-verification"
        private const val VERIFY_EMAIL_ENDPOINT = "/auth/verify"
        private const val FORGOT_PASSWORD_ENDPOINT = "/auth/forgot-password"
        private const val RESET_PASSWORD_ENDPOINT = "/auth/reset-password"
        private const val CHANGE_PASSWORD_ENDPOINT = "/auth/change-password"
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

    override suspend fun login(
        email: String,
        password: String
    ): Result<AuthInfo, DataError.Remote> {
        return httpClient.post<LoginRequest, AuthInfoSerializable>(
            route = LOGIN_ENDPOINT,
            body = LoginRequest(
                email = email,
                password = password
            )
        ).map {
            it.toDomain()
        }
    }

    override suspend fun forgotPassword(email: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ForgotPasswordRequest, Unit>(
            route = FORGOT_PASSWORD_ENDPOINT,
            body = ForgotPasswordRequest(
                email = email
            )
        )
    }

    override suspend fun resetPassword(
        newPassword: String,
        token: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = RESET_PASSWORD_ENDPOINT,
            body = ResetPasswordRequest(
                newPassword = newPassword,
                token = token
            )
        )
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): EmptyResult<DataError.Remote> {
        return httpClient.post(
            route = RESET_PASSWORD_ENDPOINT,
            body = ChangePasswordRequest(
                newPassword = newPassword,
                oldPassword = oldPassword
            )
        )
    }

}