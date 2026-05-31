package com.twugteam.admin.chat.data.participant

import com.twugteam.admin.chat.data.dto.ChatParticipantDto
import com.twugteam.admin.chat.data.dto.request.ConfirmProfilePictureRequest
import com.twugteam.admin.chat.data.dto.respone.ProfilePictureUploadUrlResponse
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.domain.models.ProfilePictureUploadUrls
import com.twugteam.admin.chat.domain.participant.ChatParticipantService
import com.twugteam.admin.core.data.networking.delete
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.data.networking.post
import com.twugteam.admin.core.data.networking.safeCall
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class KtorChatParticipantService(
    private val httpClient: HttpClient
) : ChatParticipantService {

    companion object {
        private const val CHAT_PARTICIPANTS_ENDPOINT = "/participants"
        private const val DELETE_PROFILE_PICTURE_ENDPOINT = "/participants/profile-picture"
        private const val PROFILE_PICTURE_UPLOAD_ENDPOINT = "/participants/profile-picture-upload"
        private const val CONFIRM_PROFILE_PICTURE_ENDPOINT = "/participants/confirm-profile-picture"
    }

    override suspend fun searchParticipant(searchQuery: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = CHAT_PARTICIPANTS_ENDPOINT,
            queryParams = mapOf(
                "query" to searchQuery
            )
        ).map {
            it.toDomain()
        }
    }

    override suspend fun fetchLocalParticipant(): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = CHAT_PARTICIPANTS_ENDPOINT,
        ).map {
            it.toDomain()
        }
    }

    override suspend fun getProfilePictureUploadUrl(mimeType: String): Result<ProfilePictureUploadUrls, DataError.Remote> {
        return httpClient.post<Unit, ProfilePictureUploadUrlResponse>(
            route = PROFILE_PICTURE_UPLOAD_ENDPOINT,
            body = Unit,
            queryParams = mapOf(
                "mimeType" to mimeType
            )
        ).map { it.toDomain() }
    }

    override suspend fun uploadProfilePicture(
        uploadUrl: String,
        imageBytes: ByteArray,
        headers: Map<String, String>
    ): EmptyResult<DataError.Remote> {
        return safeCall {
            httpClient.put {
                url(uploadUrl)
                setBody(imageBytes)
                headers.forEach { (key, value) ->
                    header(key, value)
                }
            }
        }
    }

    override suspend fun confirmProfilePictureUpload(publicUrl: String): EmptyResult<DataError.Remote> {
        return httpClient.post<ConfirmProfilePictureRequest, Unit>(
            route = CONFIRM_PROFILE_PICTURE_ENDPOINT,
            body = ConfirmProfilePictureRequest(publicUrl)
        )
    }

    override suspend fun deleteProfilePicture(): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = DELETE_PROFILE_PICTURE_ENDPOINT
        )
    }
}