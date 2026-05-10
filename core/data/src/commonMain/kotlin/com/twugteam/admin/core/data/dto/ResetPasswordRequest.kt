package com.twugteam.admin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val newPassword: String,
    val token: String
)
