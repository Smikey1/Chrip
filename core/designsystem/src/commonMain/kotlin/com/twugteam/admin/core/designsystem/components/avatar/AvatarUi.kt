package com.twugteam.admin.core.designsystem.components.avatar

data class AvatarUi(
    val userId: String,
    val username: String,
    val imageUrl: String? = null
) {
    val initial: String
        get() = username.take(2)
}
