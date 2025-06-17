package com.edurda77.impuls.tele_tv.data.repository

import com.edurda77.impuls.tele_tv.data.remote.VersionResponse
import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_URL
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadRepositoryImpl(
    private val httpClient: HttpClient
) : DownloadRepository {

    override suspend fun getLastUpdateVersion(): ResultWork<LastVersionApp, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("${DOWNLOAD_URL}version") {
                    contentType(ContentType.Application.Json)
                }.call
                if (response.response.status.isSuccess()) {
                    val content = response.response.body<VersionResponse>()
                    val version =
                        (content.groupResponseDto.name + "." + content.name).toDoubleOrNull()
                    if (version == null) {
                        ResultWork.Error(DataError.Network.UNKNOWN)
                    } else {
                        val lastVersionApp = LastVersionApp(
                            name = content.groupResponseDto.appResponseDto.name,
                            lastVersion = version
                        )
                        ResultWork.Success(lastVersionApp)
                    }
                } else {
                    ResultWork.Error(DataError.Network.UNKNOWN)
                }
            } catch (e: ClientRequestException) {
                when (e.response.status.value) {
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