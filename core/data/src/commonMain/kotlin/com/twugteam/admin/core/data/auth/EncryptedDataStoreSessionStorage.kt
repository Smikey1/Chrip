package com.twugteam.admin.core.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.twugteam.admin.core.data.mapper.toDomain
import com.twugteam.admin.core.data.mapper.toSerializable
import com.twugteam.admin.core.domain.auth.AuthInfo
import com.twugteam.admin.core.domain.auth.SessionStorage
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EncryptedDataStoreSessionStorage(
    private val kSafe: KSafe
) : SessionStorage {

    private val authInfoKey = "KEY_AUTH_INFO"
    override suspend fun setAuthInfo(authInfo: AuthInfo?) {
        if (authInfo == null) {
            kSafe.delete(authInfoKey)
            return
        }
        kSafe.put(authInfoKey, authInfo.toSerializable())
    }

    override fun observeAuthInfo(): Flow<AuthInfo?> {
        return kSafe.getFlow<AuthInfoSerializable?>(
            key = authInfoKey,
            defaultValue = null,
        ).map {
            it?.toDomain()
        }
    }

}