package com.starhoop.hoopstar.data.remote

import com.starhoop.hoopstar.core.DataResult
import com.starhoop.hoopstarai.data.remote.ApiErrorParser
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    fallbackMessage: String = "Something went wrong, please try again.",
    block: suspend () -> T
): DataResult<T> {
    return try {
        DataResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        val body = e.response()?.errorBody()?.string()
        DataResult.Error(ApiErrorParser.parse(body, fallbackMessage), e.code())
    } catch (e: IOException) {
        DataResult.Error("Network error. Check your connection and try again.")
    } catch (e: Exception) {
        DataResult.Error(e.message ?: fallbackMessage)
    }
}