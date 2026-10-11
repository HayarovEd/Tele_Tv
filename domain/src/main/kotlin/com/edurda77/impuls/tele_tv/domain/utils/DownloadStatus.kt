package com.edurda77.impuls.tele_tv.domain.utils

sealed class DownloadStatus {
    data object Started : DownloadStatus()

    class InProgress(val percentage: Long) : DownloadStatus()

    data object Success : DownloadStatus()

    class Error(val error: DataError) : DownloadStatus()
}