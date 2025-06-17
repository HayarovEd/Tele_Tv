package com.edurda77.impuls.tele_tv.channels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.DownloadRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.repository.ServoceRepository
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
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

class ChannelsScreenViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val downloadRepository: DownloadRepository,
    private val servoceRepository: ServoceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ChannelsScreenState())
    val state = _state
        .onStart {
            getInitialData()
            checkEnableUpdates()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ChannelsScreenState()
        )


    private val _eventFlow = MutableSharedFlow<UiChannelsEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: ChannelsScreenAction) {
        when (action) {
            is ChannelsScreenAction.UpdateFocusedIndex -> {
                _state.value.copy(
                    focusedIndex = action.index,
                )
                    .updateState()
            }

            ChannelsScreenAction.SaveSelectedChannel -> {
                saveLastChannel()
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
                    focusedIndex = it
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
                when (val resultTvChannels = remoteRepository.downloadPlaylist(
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
                        if (resultTvChannels.data.isNotEmpty()&& state.value.focusedIndex == -1) {
                            _state.value.copy(
                                focusedIndex = 0
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
            when (val result = downloadRepository.getLastUpdateVersion()) {
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
                    val currentVersion = servoceRepository.getVersionName()
                    Log.d("REST TELE TV", "release ${result.data.lastVersion}")
                    Log.d("REST TELE TV", "currentVersion $currentVersion")
                    currentVersion?.let {
                        _state.value.copy(
                            enableUpdate = currentVersion<result.data.lastVersion
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun saveLastChannel() {
        viewModelScope.launch {
            dataStoreRepository.saveLastChannel(state.value.focusedIndex)
            delay(300)
            _eventFlow.emit(UiChannelsEvents.PlayerNavigationEvent)
        }
    }

    private fun ChannelsScreenState.updateState() {
        _state.update {
            this
        }
    }
}