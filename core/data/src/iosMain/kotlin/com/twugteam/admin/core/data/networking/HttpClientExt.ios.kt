package com.twugteam.admin.core.data.networking

import com.twugteam.admin.core.domain.utils.DataError
import com.twugteam.admin.core.domain.utils.Result
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.network.sockets.SocketTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import platform.Foundation.NSURLErrorCallIsActive
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDataNotAllowed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorInternationalRoamingOff
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorResourceUnavailable
import platform.Foundation.NSURLErrorTimedOut
import kotlin.coroutines.cancellation.CancellationException

actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: DarwinHttpRequestException) {
        handleDarwinException(e)
    } catch (e: UnresolvedAddressException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: SocketTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: HttpRequestTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        Result.Failure(DataError.Remote.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}

private fun handleDarwinException(exception: DarwinHttpRequestException): Result<Nothing, DataError.Remote> {
    val nsError = exception.origin

    return if (nsError.domain == NSURLErrorDomain) {
        when (nsError.code) {
            NSURLErrorNotConnectedToInternet,
            NSURLErrorNetworkConnectionLost,
            NSURLErrorCannotFindHost,
            NSURLErrorDNSLookupFailed,
            NSURLErrorResourceUnavailable,
            NSURLErrorCallIsActive,
            NSURLErrorDataNotAllowed,
            NSURLErrorInternationalRoamingOff -> Result.Failure(DataError.Remote.NO_INTERNET)
            NSURLErrorTimedOut -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
            else -> Result.Failure(DataError.Remote.UNKNOWN)
        }
    } else {
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}