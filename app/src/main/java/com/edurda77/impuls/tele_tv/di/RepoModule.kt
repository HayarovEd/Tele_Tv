package com.edurda77.impuls.tele_tv.di

import com.edurda77.impuls.tele_tv.data.repository.DataStoreRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.DownloadRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.RemoteRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.ServoceRepositoryImpl
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.repository.ServoceRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
    singleOf(::RemoteRepositoryImpl) { bind<RemoteRepository>() }
    singleOf(::DataStoreRepositoryImpl) { bind<DataStoreRepository>() }
    singleOf(::DownloadRepositoryImpl) { bind<DownloadRepository>() }
    singleOf(::ServoceRepositoryImpl) { bind<ServoceRepository>() }

}
