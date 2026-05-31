package com.twugteam.admin.chat.data.participant

import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.chat.domain.participant.ChatParticipantRepository
import com.twugteam.admin.chat.domain.participant.ChatParticipantService
import com.twugteam.admin.core.domain.auth.SessionStorage
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.first

class OfflineFirstChatParticipantRepository(
    private val chatParticipantService: ChatParticipantService,
    private val sessionStorage: SessionStorage,
) : ChatParticipantRepository {
    override suspend fun searchParticipant(searchQuery: String): Result<ChatParticipant, DataError.Remote> {
        return chatParticipantService.searchParticipant(searchQuery)
    }

    override suspend fun getLocalParticipant(): Result<ChatParticipant, DataError> {
        return chatParticipantService
            .fetchLocalParticipant()
            .onSuccess { participant ->
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.setAuthInfo(
                    authInfo = currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            id = participant.userId,
                            username = participant.username,
                            profilePictureUrl = participant.profilePictureUrl
                        )
                    )
                )

            }
    }

    override suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote> {
        val result = chatParticipantService.getProfilePictureUploadUrl(mimeType)
        if (result is Result.Failure) {
            return result
        }
        val uploadUrls = (result as Result.Success).data
        val uploadResult = chatParticipantService.uploadProfilePicture(
            uploadUrl = uploadUrls.uploadUrl,
            imageBytes = imageBytes,
            headers = uploadUrls.headers
        )

        if (uploadResult is Result.Failure) {
            return uploadResult
        }

        return chatParticipantService
            .confirmProfilePictureUpload(uploadUrls.publicUrl)
            .onSuccess {
                val currentAuthInfo = sessionStorage.observeAuthInfo().first()
                sessionStorage.setAuthInfo(
                    authInfo = currentAuthInfo?.copy(
                        user = currentAuthInfo.user.copy(
                            profilePictureUrl = uploadUrls.publicUrl
                        )
                    )
                )

            }
    }
}