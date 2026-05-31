package com.twugteam.admin.chat.data.dto.respone

import io.ktor.http.Headers
import kotlinx.serialization.Serializable

@Serializable
data class ProfilePictureUploadUrlResponse(
    val uploadUrl: String,
    val publicUrl: String,
    val headers: Map<String, String>
)
