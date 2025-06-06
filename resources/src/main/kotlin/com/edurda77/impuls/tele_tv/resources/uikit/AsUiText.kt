package com.edurda77.impuls.tele_tv.resources.uikit

import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.resources.R
import com.edurda77.resources.uikit.UiText


fun DataError.asUiText(): UiText {
    return when (this) {

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )


        DataError.Network.UNAUTHORIZED -> UiText.StringResource(
            R.string.error_autorization
        )
        DataError.Network.BAD_REQUEST -> {
            UiText.StringResource(
                R.string.not_unique_id
            )
        }
        DataError.DataStore.ERROR_READ_DATA -> {
            UiText.StringResource(
                R.string.read_error_data
            )
        }

        DataError.DataStore.ERROR_WRITE_DATA -> {
            UiText.StringResource(
                R.string.write_error_data
            )
        }
        DataError.SerializationError.FORMAT_ERROR -> {
            UiText.StringResource(
                R.string.error_take_data
            )
        }


        DataError.LocalDateBase.ERROR_READ_DATA -> {
            UiText.StringResource(
                R.string.read_error_data
            )
        }

        DataError.LocalDateBase.ERROR_WRITE_DATA -> {
            UiText.StringResource(
                R.string.read_error_data
            )
        }

        DataError.Network.REQUEST_TIMEOUT -> {
            UiText.StringResource(
                R.string.request_timeout
            )
        }
    }
}

fun ResultWork.Error<*, DataError>.asErrorUiText(): UiText {
    return error.asUiText()
}