package com.edurda77.impuls.tele_tv.domain.repository

interface ServiceRepository {
    fun getVersionName(): Double?
    fun setWakeLock()
    fun releaseWakeLock()
}