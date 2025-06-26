package com.edurda77.impuls.tele_tv.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {


    private val _eventFlow = MutableSharedFlow<UiSplashEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            if(dataStoreRepository.getCredintial()==null) {
                _eventFlow.emit(UiSplashEvents.LoginNavigationEvent)
            } else  {
                delay(300)
                _eventFlow.emit(UiSplashEvents.ChannelsNavigationEvent)
            }
        }
    }

}