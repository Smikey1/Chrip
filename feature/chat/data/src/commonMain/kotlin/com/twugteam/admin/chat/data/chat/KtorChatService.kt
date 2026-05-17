package com.twugteam.admin.chat.data.chat

import com.twugteam.admin.chat.data.dto.ChatDto
import com.twugteam.admin.chat.data.dto.request.AddParticipantToChatRequest
import com.twugteam.admin.chat.data.dto.request.CreateChatRequest
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.domain.chat.ChatService
import com.twugteam.admin.chat.domain.models.Chat
import com.twugteam.admin.core.data.networking.delete
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.data.networking.post
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.asEmptyDataResult
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient

class KtorChatService(
    private val httpClient: HttpClient
) : ChatService {

    companion object {
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
        ).map { chatDtos ->
            chatDtos.map {
                it.toDomain()
            }
        }
    }

    override suspend fun fetchChatById(chatId: String): Result<Chat, DataError.Remote> {
        return httpClient.get<ChatDto>(
            route = "$CHAT_ENDPOINT/$chatId",
        ).map {
            it.toDomain()
        }
    }

    override suspend fun leaveChat(chatId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete<Unit>(
            route = "$CHAT_ENDPOINT/$chatId/leave",
        ).asEmptyDataResult()
    }

    override suspend fun addParticipantsToChat(
        chatId: String,
        otherUserIds: List<String>
    ): Result<Chat, DataError.Remote> {
        return httpClient.post<AddParticipantToChatRequest, ChatDto>(
            route = "$CHAT_ENDPOINT/$chatId/add",
            body = AddParticipantToChatRequest(
                userIds = otherUserIds
            )
        ).map {
            it.toDomain()
        }
    }
}