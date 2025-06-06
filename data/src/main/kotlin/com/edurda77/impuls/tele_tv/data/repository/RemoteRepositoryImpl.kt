package com.edurda77.impuls.tele_tv.data.repository

import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.basicAuth
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteRepositoryImpl(
    private val httpClient: HttpClient
): RemoteRepository {

    override suspend fun authorization(
        username: String,
        password: String,
    ): ResultWork<Unit, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("login") {
                    contentType(ContentType.Application.Json)
                    basicAuth(
                        username = username,
                        password = password
                    )
                }.call
                if (response.response.status.isSuccess()) {
                    ResultWork.Success(Unit)
                } else {
                    ResultWork.Error(DataError.Network.UNAUTHORIZED)
                }
            } catch (e: ClientRequestException) {
                when (e.response.status.value) {
                    403 -> ResultWork.Error(DataError.Network.UNAUTHORIZED)
                    else -> ResultWork.Error(DataError.Network.UNKNOWN)
                }
            } catch (e: ServerResponseException) {
                e.printStackTrace()
                ResultWork.Error(DataError.Network.SERVER_ERROR)
            } catch (e: HttpRequestTimeoutException) {
                e.printStackTrace()
                ResultWork.Error(DataError.Network.REQUEST_TIMEOUT)
            } catch (e: JsonConvertException) {
                e.printStackTrace()
                ResultWork.Error(DataError.SerializationError.FORMAT_ERROR)
            } catch (e: Exception) {
                e.printStackTrace()
                ResultWork.Error(DataError.Network.UNKNOWN)
            }
        }
    }
}