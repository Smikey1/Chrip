package com.twugteam.admin.core.data.database

import androidx.sqlite.SQLiteException
import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result

suspend inline fun <T> safeDatabaseUpdate(update: suspend () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(update())
    } catch (ex: SQLiteException) {
        Result.Failure(DataError.Local.DISK_FULL)
    }
}