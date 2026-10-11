package com.edurda77.base_module.di

import com.edurda77.impuls.tele_tv.data.repository.DataStoreRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.DownloadRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.InstallerImpl
import com.edurda77.impuls.tele_tv.data.repository.LocalRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.RemoteRepositoryImpl
import com.edurda77.impuls.tele_tv.data.repository.ServiceRepositoryImpl
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.repository.Installer
import com.edurda77.impuls.tele_tv.domain.repository.LocalRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.repository.ServiceRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
    singleOf(::RemoteRepositoryImpl) { bind<RemoteRepository>() }
    singleOf(::DataStoreRepositoryImpl) { bind<DataStoreRepository>() }
    singleOf(::DownloadRepositoryImpl) { bind<DownloadRepository>() }
    singleOf(::ServiceRepositoryImpl) { bind<ServiceRepository>() }
    singleOf(::InstallerImpl) { bind<Installer>() }
    singleOf(::LocalRepositoryImpl) { bind<LocalRepository>() }

}
