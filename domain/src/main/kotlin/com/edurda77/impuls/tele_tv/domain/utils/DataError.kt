package com.edurda77.impuls.tele_tv.domain.utils

sealed interface DataError : RootError {
    enum class Network: DataError {
        UNAUTHORIZED,
        SERVER_ERROR,
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNKNOWN
    }

    enum class SerializationError: DataError {
       FORMAT_ERROR,
    }

    enum class  DataStore: DataError {
        ERROR_READ_DATA,
        ERROR_WRITE_DATA
    }

    enum class LocalDateBase : DataError {
        ERROR_READ_DATA,
        ERROR_WRITE_DATA
    }
}