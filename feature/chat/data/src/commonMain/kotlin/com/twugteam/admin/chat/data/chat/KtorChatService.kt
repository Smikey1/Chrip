package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.dto.ChatDto
import com.twugteam.admin.chat.data.dto.request.CreateChatRequest
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.domain.chat.ChatService
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.data.networking.post
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post

class KtorChatService(
    private val httpClient: HttpClient
): ChatService {

    companion object{
        private const val CHAT_ENDPOINT = "/chat"
    }
    override suspend fun createChat(otherUserIds: List<String>): Result<Chat, DataError.Remote> {
        return httpClient.post<CreateChatRequest, ChatDto>(
            route = CHAT_ENDPOINT,
            body = CreateChatRequest(
                otherUserIds = otherUserIds
            )
        ).map {
            it.toDomain()
        }
    }

    override suspend fun fetchChats(): Result<List<Chat>, DataError.Remote> {
        return httpClient.get<List<ChatDto>>(
            route = CHAT_ENDPOINT
        ).map {chatDtos ->
            chatDtos.map {
                it.toDomain()
            }
        }
    }
}