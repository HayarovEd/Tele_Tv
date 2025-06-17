package com.edurda77.impuls.tele_tv.domain.repository

import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.utils.DataError
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork

interface DownloadRepository {
    suspend fun getLastUpdateVersion(): ResultWork<LastVersionApp, DataError>
}