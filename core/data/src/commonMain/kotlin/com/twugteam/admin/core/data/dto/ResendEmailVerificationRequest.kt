package com.twugteam.admin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResendEmailVerificationRequest(
    val email: String
)
