package com.edurda77.impuls.tele_tv.splash

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val isMobile = savedStateHandle.toRoute<NavigationRoute.Splash>().isMobile

    private val _eventFlow = MutableSharedFlow<UiSplashEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            if (isMobile) {
                if (dataStoreRepository.getCredintial() == null) {
                    _eventFlow.emit(UiSplashEvents.LoginMobileNavigationEvent)
                } else {
                    delay(300)
                    _eventFlow.emit(UiSplashEvents.ChannelsMobileNavigationEvent)
                }
            } else  {
                if(dataStoreRepository.getCredintial()==null) {
                    _eventFlow.emit(UiSplashEvents.LoginNavigationEvent)
                } else  {
                    delay(300)
                    _eventFlow.emit(UiSplashEvents.ChannelsNavigationEvent)
                }
            }
        }
    }

}