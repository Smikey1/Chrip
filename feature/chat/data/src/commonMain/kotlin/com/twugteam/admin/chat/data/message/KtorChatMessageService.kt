package com.twugteam.admin.chat.data.message

import com.twugteam.admin.chat.data.dto.ChatMessageDto
import com.twugteam.admin.chat.data.mappers.toDomain
import com.twugteam.admin.chat.domain.message.ChatMessageService
import com.twugteam.admin.chat.domain.models.ChatMessage
import com.twugteam.admin.core.data.networking.get
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import com.twugteam.admin.core.domain.utils.map
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
) : ChatMessageService {

    override suspend fun fetchMessage(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstant.PAGE_SIZE
                if (before != null) {
                    this["before"] = before
                }
            }
        ).map { chatMessages ->
            chatMessages.map {
                it.toDomain()
            }
        }
    }

}