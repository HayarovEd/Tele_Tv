package com.edurda77.impuls.tele_tv.channels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.repository.Installer
import com.edurda77.impuls.tele_tv.domain.repository.LocalRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.repository.ServiceRepository
import com.edurda77.impuls.tele_tv.domain.utils.APK_EXT
import com.edurda77.impuls.tele_tv.domain.utils.DELAY_MINUTE
import com.edurda77.impuls.tele_tv.domain.utils.DownloadStatus
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ChannelsScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val downloadRepository: DownloadRepository,
    private val serviceRepository: ServiceRepository,
    private val installer: Installer,
    private val localRepository: LocalRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChannelsScreenState())
    val state = _state
        .onStart {
            getInitialData()
            getLastChannels()
            checkEnableUpdates()
            getCurrentTime()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChannelsScreenState()
        )

    private val downloadUrl = savedStateHandle.toRoute<NavigationRoute.Channels>().downloadUrl

    private val _eventFlow = MutableSharedFlow<UiChannelsEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: ChannelsScreenAction) {
        when (action) {
            is ChannelsScreenAction.UpdateFocusedIndex -> {
                _state.value.copy(
                    focusedChannelId = action.id,
                )
                    .updateState()
            }

            ChannelsScreenAction.SaveSelectedChannel -> {
                saveLastChannel()
            }

            ChannelsScreenAction.DownloadUpdate -> {
                downloadAndInstall()
            }

            ChannelsScreenAction.Logout -> {
                logout()
            }
        }
    }


    private fun getInitialData() {
        _state.value.copy(
            isLoading = true,
        )
            .updateState()
        viewModelScope.launch {
            dataStoreRepository.getLastChannel()?.let {
                _state.value.copy(
                    focusedChannelId = it
                )
                    .updateState()
            }
        }
        viewModelScope.launch {
            val credintial = dataStoreRepository.getCredintial()
            _state.value.copy(
                credintial = credintial,
            )
                .updateState()
            credintial?.let {
                when (val resultTvChannels = remoteRepository.getTvChannels(
                    username = credintial.username,
                    password = credintial.password
                )) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            isLoading = false,
                            message = resultTvChannels.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            isLoading = false,
                            tvChannels = resultTvChannels.data
                        )
                            .updateState()
                        if (resultTvChannels.data.isNotEmpty() && state.value.focusedChannelId == null) {
                            _state.value.copy(
                                focusedChannelId = resultTvChannels.data.first().tvgId
                            )
                                .updateState()
                        }
                    }
                }
            }
        }
    }

    private fun checkEnableUpdates() {
        viewModelScope.launch {
            when (val result = downloadRepository.getLastUpdateVersion(downloadUrl)) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        release = result.data
                    )
                        .updateState()
                    val currentVersion = serviceRepository.getVersionName()
                    //Log.d("REST TELE TV", "release ${result.data.lastVersion}")
                    //Log.d("REST TELE TV", "currentVersion $currentVersion")
                    currentVersion?.let {
                        _state.value.copy(
                            enableUpdate = currentVersion < result.data.lastVersion
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun downloadAndInstall() {
        viewModelScope.launch {
            state.value.release?.let { release ->
                val fileName = "${release.name}-${release.lastVersion}.$APK_EXT"
                downloadRepository.downloadFile(
                    downloadUrl = downloadUrl,
                    downloadedFileName = fileName
                ).collect { collector ->
                    when (collector) {
                        is DownloadStatus.Error -> {
                            _state.value.copy(
                                message = collector.error.asUiText(),
                                isUpdating = false
                            )
                                .updateState()
                        }

                        is DownloadStatus.InProgress -> {
                            _state.value.copy(
                                percentDownload = collector.percentage.toInt()
                            )
                                .updateState()
                        }

                        DownloadStatus.Started -> {
                            _state.value.copy(
                                isUpdating = true
                            )
                                .updateState()
                        }

                        DownloadStatus.Success -> {
                            _state.value.copy(
                                isUpdating = false,
                                enableUpdate = false
                            )
                                .updateState()
                            installer.installAPK(fileName)
                        }
                    }
                }
            }
        }
    }

    private fun saveLastChannel() {
        viewModelScope.launch {
            state.value.focusedChannelId?.let {
                dataStoreRepository.saveLastChannel(it)
                state.value.scrolledIndex?.let { index ->
                    localRepository.insertLocation(state.value.tvChannels[index])
                }
                delay(300)
                _eventFlow.emit(UiChannelsEvents.PlayerNavigationEvent)
            }
        }
    }

    private fun getLastChannels() {
        viewModelScope.launch {
            localRepository.getAllChannels().collect { collector ->
                when (collector) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            message = collector.error.asUiText(),
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            lastTvChannels = collector.data
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getCurrentTime() {
        viewModelScope.launch {
            while (true) {
                delay(DELAY_MINUTE)
                _state.value.copy(
                    currentTime = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).time
                )
                    .updateState()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            dataStoreRepository.clearCredintial()
            _eventFlow.emit(UiChannelsEvents.LoginNavigationEvent)
        }
    }

    private fun ChannelsScreenState.updateState() {
        _state.update {
            this
        }
    }
}