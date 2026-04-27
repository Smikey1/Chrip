package com.twugteam.admin.core.presentation.util

import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.presentation.Res
import com.twugteam.admin.core.presentation.error_bad_request
import com.twugteam.admin.core.presentation.error_conflict
import com.twugteam.admin.core.presentation.error_disk_full
import com.twugteam.admin.core.presentation.error_forbidden
import com.twugteam.admin.core.presentation.error_no_internet
import com.twugteam.admin.core.presentation.error_not_found
import com.twugteam.admin.core.presentation.error_payload_too_large
import com.twugteam.admin.core.presentation.error_request_timeout
import com.twugteam.admin.core.presentation.error_serialization
import com.twugteam.admin.core.presentation.error_server
import com.twugteam.admin.core.presentation.error_service_unavailable
import com.twugteam.admin.core.presentation.error_too_many_requests
import com.twugteam.admin.core.presentation.error_unauthorized
import com.twugteam.admin.core.presentation.error_unknown

fun DataError.toUiText(): UiText {
    val resource = when(this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.NOT_FOUND -> Res.string.error_not_found
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
        DataError.Remote.FORBIDDEN -> Res.string.error_forbidden
        DataError.Remote.NOT_FOUND -> Res.string.error_not_found
        DataError.Remote.CONFLICT -> Res.string.error_conflict
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.PAYLOAD_TOO_LARGE -> Res.string.error_payload_too_large
        DataError.Remote.SERVER_ERROR -> Res.string.error_server
        DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
    return UiText.Resource(resource)
}