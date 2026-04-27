package com.twugteam.admin.core.domain.auth

import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.EmptyResult

interface AuthService {
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): EmptyResult<DataError.Remote>
}