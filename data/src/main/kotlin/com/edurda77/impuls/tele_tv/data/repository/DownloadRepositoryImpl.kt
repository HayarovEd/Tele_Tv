package com.edurda77.impuls.tele_tv.data.repository

import android.app.Application
import com.edurda77.impuls.tele_tv.data.handler.pathToDownloadFile
import com.edurda77.impuls.tele_tv.data.remote.VersionResponse
import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.utils.DOWNLOAD_TV_URL
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.DownloadStatus
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File

class DownloadRepositoryImpl(
    private val httpClient: HttpClient,
    private val application: Application
) : DownloadRepository {

    override suspend fun getLastUpdateVersion(): ResultWork<LastVersionApp, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("${DOWNLOAD_TV_URL}version") {
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

    override suspend fun downloadFile(
        downloadedFileName: String,
    ): Flow<DownloadStatus> {
        return callbackFlow {
            val apkFilePath =
                application.pathToDownloadFile(downloadedFileName)
            try {
                send(DownloadStatus.Started)
                val response = httpClient.get("${DOWNLOAD_TV_URL}file") {
                    contentType(ContentType.Application.Json)
                    onDownload { bytesSentTotal, contentLength ->
                        contentLength?.let {
                            send(DownloadStatus.InProgress(bytesSentTotal*100/contentLength))
                        }
                    }
                }
                if (response.status.isSuccess()) {
                    val file =
                        File(apkFilePath)
                    response.bodyAsChannel().copyAndClose(file.writeChannel())
                    send(DownloadStatus.Success)
                } else {
                    send(DownloadStatus.Error(DataError.Network.BAD_REQUEST))
                }
                close()
            } catch (e: Exception) {
                send(DownloadStatus.Error(DataError.Network.SERVER_ERROR))
            }
        }
    }
}