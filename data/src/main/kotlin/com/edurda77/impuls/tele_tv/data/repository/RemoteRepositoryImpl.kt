package com.edurda77.impuls.tele_tv.data.repository

import com.edurda77.impuls.tele_tv.data.remote.ChannelListResponse
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
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
                /*val response = httpClient.post("api/channel/grid") {
                    contentType(ContentType.Application.Json)
                    basicAuth(
                        username = username,
                        password = password
                    )
                }.call*/
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

    /*private suspend fun parseM3U(m3uContent: String): List<TvChannel> {
        return withContext(Dispatchers.Default) {
            val tvChannels = mutableListOf<TvChannel>()
            val lines = m3uContent.lines()

            var i = 0
            while (i < lines.size) {
                val line = lines[i]
                if (line.startsWith("#EXTINF")) {
                    val metadata = parseExtInf(line)
                    val url = lines.getOrNull(i + 1)?.takeIf { it.startsWith("http") } ?: ""

                    if (url.isNotEmpty()) {
                        tvChannels.add(
                            TvChannel(
                                tvgId = metadata["tvg-id"] ?: "",
                                tvgLogo = metadata["tvg-logo"],
                                tvgChannelNumber = metadata["tvg-chno"] ?: "",
                                name = metadata["name"] ?: "",
                                url = url
                            )
                        )
                    }
                    i += 2 // Skip the URL line
                } else {
                    i++
                }
            }
            tvChannels
        }
    }

    private fun parseExtInf(extInfLine: String): Map<String, String?> {
        val metadata = mutableMapOf<String, String?>()
        val parts = extInfLine.split(" ", limit = 2)

        val attributes = parts.getOrNull(1)?.split(" ") ?: emptyList()
        attributes.forEach { attr ->
            if (attr.contains("=")) {
                val (key, value) = attr.split("=", limit = 2)
                metadata[key] = value.removeSurrounding("\"")
            }
        }
        val name = extInfLine.substringAfterLast(",").trim()
        if (name.isNotEmpty()) {
            metadata["name"] = name
        }
        return metadata
    }*/
}