package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.dto.ChatParticipantDto
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.domain.chat.ChatParticipantService
import com.twugteam.admin.chat.domain.models.ChatParticipant
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class KtorChatParticipantService(
    private val httpClient: HttpClient
) : ChatParticipantService {

    companion object{
        private const val SEARCH_ENDPOINT = "/participants"
    }

    override suspend fun searchParticipant(searchQuery: String): Result<ChatParticipant, DataError.Remote> {
        return httpClient.get<ChatParticipantDto>(
            route = SEARCH_ENDPOINT,
            queryParams = mapOf(
                "query" to searchQuery
            )
        ).map {
            it.toDomain()
        }
    }
}