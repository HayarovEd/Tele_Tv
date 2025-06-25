package com.edurda77.impuls.tele_tv.data.repository

import com.edurda77.impuls.tele_tv.data.handler.convertToTvEpg
import com.edurda77.impuls.tele_tv.data.remote.ChannelListResponse
import com.edurda77.impuls.tele_tv.data.remote.EpgDataResponse
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.utils.BASE_URL
import com.edurda77.impuls.tele_tv.domain.utils.CHANNEL_URL_PREFIX
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoteRepositoryImpl(
    private val httpClient: HttpClient
) : RemoteRepository {

    override suspend fun authorization(
        username: String,
        password: String,
    ): ResultWork<Unit, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("login") {
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

    override suspend fun getTvChannels(
        username: String,
        password: String,
    ): ResultWork<List<TvChannel>, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.submitForm (
                    url = "api/channel/grid",
                    formParameters = parameters {
                        append("limit", "500")
                        append("sort", "number")
                    }
                ) {
                    method = HttpMethod.Post
                    basicAuth(
                        username = username,
                        password = password
                    )
                }.call
                if (response.response.status.isSuccess()) {
                    val successResponse = response.response.body<ChannelListResponse>()
                    val channels = successResponse.entries.map {
                        TvChannel(
                            tvgChannelNumber = it.number,
                            name = it.name,
                            url = "$BASE_URL$CHANNEL_URL_PREFIX${it.uuid}",
                            tvgLogo = it.iconPublicUrl,
                            tvgId = it.uuid
                        )
                    }
                    ResultWork.Success(channels)
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

    override suspend fun getEpg(
        username: String,
        password: String,
        dir: String,
        start: Int,
        limit: Int
    ): ResultWork<List<TvEpg>, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.submitForm (
                    url = "api/epg/events/grid",
                    formParameters = parameters {
                        append("dir", dir)
                        append("start", "$start")
                        append("limit", "$limit")
                    }
                ) {
                    method = HttpMethod.Post
                    basicAuth(
                        username = username,
                        password = password
                    )
                }.call
                if (response.response.status.isSuccess()) {
                    val successResponse = response.response.body<EpgDataResponse>()
                    ResultWork.Success(successResponse.convertToTvEpg())
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

    override suspend fun getEpgByChannelId(
        username: String,
        password: String,
        dir: String,
        start: Int,
        limit: Int,
        channelId: String
    ): ResultWork<List<TvEpg>, DataError> {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.submitForm (
                    url = "api/epg/events/grid",
                    formParameters = parameters {
                        append("dir", dir)
                        append("start", "$start")
                        append("limit", "$limit")
                    }
                ) {
                    method = HttpMethod.Post
                    basicAuth(
                        username = username,
                        password = password
                    )
                }.call
                if (response.response.status.isSuccess()) {
                    val successResponse = response.response.body<EpgDataResponse>()
                    ResultWork.Success(successResponse.convertToTvEpg())
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