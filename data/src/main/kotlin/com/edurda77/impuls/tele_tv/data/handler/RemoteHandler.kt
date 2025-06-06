package com.edurda77.impuls.tele_tv.data.handler


import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException

suspend fun <D> handleResponse(data: suspend () -> D): ResultWork<D, DataError> {
    return try {
        ResultWork.Success(data())
    } catch (e: ClientRequestException) {
        when (e.response.status.value) {
            400 -> ResultWork.Error(DataError.Network.BAD_REQUEST)
            401 -> ResultWork.Error(DataError.Network.UNAUTHORIZED)
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
