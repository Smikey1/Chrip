package com.twugteam.admin.chat.data.mappers

import com.twugteam.admin.chat.data.dto.respone.ProfilePictureUploadUrlResponse
import com.twugteam.admin.chat.domain.models.ProfilePictureUploadUrls

fun ProfilePictureUploadUrlResponse.toDomain(): ProfilePictureUploadUrls {
    return ProfilePictureUploadUrls(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers
    )
}