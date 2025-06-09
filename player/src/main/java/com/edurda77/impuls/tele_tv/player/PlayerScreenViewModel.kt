package com.edurda77.impuls.tele_tv.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerScreenViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _state = MutableStateFlow(PlayerScreenState())
    val state = _state
        .onStart {
            getInitialData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerScreenState()
        )

    fun onAction(action: PlayerScreenAction) {
        when (action) {
            else -> TODO("Handle actions")
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
                        if (resultTvChannels.data.isNotEmpty()) {
                            _state.value.copy(
                                selectedIndex = 0
                            )
                                .updateState()
                        }
                    }
                }
            }
        }
    }

    private fun PlayerScreenState.updateState() {
        _state.update {
            this
        }
    }
}