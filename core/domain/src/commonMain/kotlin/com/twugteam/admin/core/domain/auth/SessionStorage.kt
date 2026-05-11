package com.twugteam.admin.core.domain.auth

import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    suspend fun setAuthInfo(authInfo: AuthInfo?)

    fun observeAuthInfo(): Flow<AuthInfo?>
}