package com.edurda77.impuls.tele_tv.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state
        .onStart {
            loadLocalData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LoginScreenState()
        )


    private val _eventFlow = MutableSharedFlow<UiLoginEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: LoginScreenAction) {
        when (action) {
            LoginScreenAction.OnLogin -> {
                processLogin()
            }

            is LoginScreenAction.OnSetPassword -> {
                _state.value.copy(
                    password = action.password,
                )
                    .updateState()
            }

            is LoginScreenAction.OnSetUsername -> {
                _state.value.copy(
                    username = action.username,
                )
                    .updateState()
            }
        }
    }

    private fun processLogin() {
        _state.value.copy(
            isLoading = true,
        )
            .updateState()
        viewModelScope.launch {
            when (val result = remoteRepository.authorization(
                username = state.value.username,
                password = state.value.password
            )) {
                is ResultWork.Error -> {
                    _state.value.copy(
                        isLoading = false,
                        message = result.error.asUiText()
                    )
                        .updateState()
                }

                is ResultWork.Success -> {
                    _state.value.copy(
                        isLoading = false,
                    )
                        .updateState()
                    viewModelScope.launch {
                        dataStoreRepository.saveCredintial(
                            username = state.value.username,
                            password = state.value.password
                        )
                    }
                    _eventFlow.emit(UiLoginEvents.ChannelsNavigationEvent)
                }
            }
        }
    }

    private fun loadLocalData() {
        viewModelScope.launch {
            val credintial = dataStoreRepository.getCredintial()
            credintial?.let {
                _state.value.copy(
                    username = credintial.username,
                    password = credintial.password
                )
                    .updateState()
            }
        }
    }

    private fun LoginScreenState.updateState() {
        _state.update {
            this
        }
    }
}