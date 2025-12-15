package com.edurda77.impuls.tele_tv.channels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
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
import com.edurda77.impuls.tele_tv.domain.utils.createCategoryChannelMap
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.coroutines.async
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

            ChannelsScreenAction.DownloadUpdate -> {
                downloadAndInstall()
            }

            ChannelsScreenAction.Logout -> {
                logout()
            }

            is ChannelsScreenAction.SaveChosenChannel -> {
                saveChannel(action.channel)
            }
        }
    }


    private fun getInitialData() {
        _state.value.copy(
            isLoading = true,
        )
            .updateState()
        viewModelScope.launch {
            val credintial = dataStoreRepository.getCredintial()
            _state.value.copy(
                credintial = credintial,
            )
                .updateState()
            credintial?.let {
                val channelsDiff = async {
                    remoteRepository.getTvChannels(
                        username = credintial.username,
                        password = credintial.password
                    )
                }
                val categoriesDiff = async {
                    remoteRepository.getCategories(
                        username = credintial.username,
                        password = credintial.password
                    )
                }
                val resultTvChannels = channelsDiff.await()
                val resultCategories = categoriesDiff.await()
                when {
                    resultTvChannels is ResultWork.Error -> {
                        _state.value = _state.value.copy(
                            message = resultTvChannels.error.asUiText(),
                            isLoading = false
                        )
                        return@launch
                    }

                    resultCategories is ResultWork.Error -> {
                        _state.value = _state.value.copy(
                            message = resultCategories.error.asUiText(),
                            isLoading = false
                        )
                        return@launch
                    }

                    resultTvChannels is ResultWork.Success && resultCategories is ResultWork.Success -> {

                        val groupedMap = createCategoryChannelMap(
                            categories = resultCategories.data,
                            channels = resultTvChannels.data
                        )

                        _state.value = _state.value.copy(
                            categories = resultCategories.data,
                            tvChannels = resultTvChannels.data,
                            groupedTvChannels = groupedMap,
                            isLoading = false,
                            message = null
                        )
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
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                )
                    .updateState()
            }
        }
    }

    private fun saveChannel(channel: TvChannel) {
        viewModelScope.launch {
            when (val result = localRepository.insertChannel(channel)) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        message = result.error.asUiText(),
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _eventFlow.emit(UiChannelsEvents.PlayerNavigationEvent(channel.tvgId))
                }
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