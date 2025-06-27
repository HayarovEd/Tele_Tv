package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.DownloadStatus
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    suspend fun getLastUpdateVersion(
        downloadUrl: String,
    ): ResultWork<LastVersionApp, DataError>

    suspend fun downloadFile(
        downloadUrl: String,
        downloadedFileName: String
    ): Flow<DownloadStatus>
}