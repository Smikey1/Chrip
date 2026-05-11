package com.twugteam.admin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    val token: String
)
