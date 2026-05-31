package com.twugteam.admin.chat.domain.participant

import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result

interface ChatParticipantRepository {
    suspend fun searchParticipant(searchQuery: String): Result<ChatParticipant, DataError>
    suspend fun getLocalParticipant(): Result<ChatParticipant, DataError>
    suspend fun uploadProfilePicture(
        imageBytes: ByteArray,
        mimeType: String
    ): EmptyResult<DataError.Remote>

}