package com.edurda77.impuls.tele_tv.player

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.LocalRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.repository.ServiceRepository
import com.edurda77.impuls.tele_tv.domain.utils.CHANNEL_LIMIT
import com.edurda77.impuls.tele_tv.domain.utils.DELAY_MINUTE
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.domain.utils.SINGLE_LIMIT
import com.edurda77.impuls.tele_tv.domain.utils.VOLUME_STEP
import com.edurda77.impuls.tele_tv.domain.utils.convertToDate
import com.edurda77.impuls.tele_tv.domain.utils.radioCh
import com.edurda77.impuls.tele_tv.resources.model.NavigationRoute
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PlayerScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {

    private var volumeJob: Job? = null
    private var menuJob: Job? = null
    private var queryJob: Job? = null
    private var drumMenuJob: Job? = null

    private val _state = MutableStateFlow(PlayerScreenState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerScreenState()
        )

    init {
        getInitialData()
        getCurrentTime()
        getCurrentVolume()
    }

    fun onAction(action: PlayerScreenAction) {
        when (action) {
            is PlayerScreenAction.OnRestartMenuTimer -> {
                startTimerVisibleMenu(action.duration)
            }

            PlayerScreenAction.DecrementTvChannel -> {
                _state.update { currentState ->
                    currentState.selectedIndex?.let { selectedIndex ->
                        if (currentState.tvChannels[selectedIndex] == currentState.tvChannels.first()) {
                            Log.d(PlayerConstants.TAG, "action 1")
                            currentState.copy(
                                selectedChannelId = currentState.tvChannels.last().tvgId,
                                focusedChannelId = currentState.tvChannels.last().tvgId,
                            )
                        } else {
                            Log.d(PlayerConstants.TAG, "action 2")
                            currentState.copy(
                                selectedChannelId = currentState.tvChannels[selectedIndex - 1].tvgId,
                                focusedChannelId = currentState.tvChannels[selectedIndex - 1].tvgId,
                            )
                        }
                    } ?: currentState
                }
                saveLastChannel()
                startTimerVisibleDrumMenu(5)
            }

            PlayerScreenAction.IncrementTvChannel -> {
                _state.update { currentState ->
                    currentState.selectedIndex?.let { selectedIndex ->
                        if (currentState.tvChannels[selectedIndex] == currentState.tvChannels.last()) {
                            Log.d(PlayerConstants.TAG, "action 3")
                            currentState.copy(
                                selectedChannelId = currentState.tvChannels.first().tvgId,
                                focusedChannelId = currentState.tvChannels.first().tvgId
                            )
                        } else {
                            Log.d(PlayerConstants.TAG, "action 4")
                            currentState.copy(
                                selectedChannelId = currentState.tvChannels[selectedIndex + 1].tvgId,
                                focusedChannelId = currentState.tvChannels[selectedIndex + 1].tvgId,
                            )
                        }
                    } ?: currentState
                }
                saveLastChannel()
                startTimerVisibleDrumMenu(5)
            }

            is PlayerScreenAction.UpdateSelectedIndex -> {
                Log.d(PlayerConstants.TAG, "action 5")
                _state.update { it.copy(selectedChannelId = it.focusedChannelId) }
                saveLastChannel()
                startTimerVisibleMenu(10)
            }

            PlayerScreenAction.DecrementFocusedIndex -> {
                _state.update { currentState ->
                    currentState.focusedIndex?.let { focusedIndex ->
                        if (currentState.tvChannels[focusedIndex] == currentState.tvChannels.first()) {
                            currentState.copy(focusedChannelId = currentState.tvChannels.last().tvgId)
                        } else {
                            currentState.copy(focusedChannelId = currentState.tvChannels[focusedIndex - 1].tvgId)
                        }
                    } ?: currentState
                }
                loadEpgByFocusedChannel()
                startTimerVisibleMenu(10)
            }

            PlayerScreenAction.IncrementFocusedIndex -> {
                _state.update { currentState ->
                    currentState.focusedIndex?.let { focusedIndex ->
                        if (currentState.tvChannels[focusedIndex] == currentState.tvChannels.last()) {
                            currentState.copy(focusedChannelId = currentState.tvChannels.first().tvgId)
                        } else {
                            currentState.copy(focusedChannelId = currentState.tvChannels[focusedIndex + 1].tvgId)
                        }
                    } ?: currentState
                }
                loadEpgByFocusedChannel()
                startTimerVisibleMenu(10)
            }

            PlayerScreenAction.ShowSideMenu -> {
                startTimerVisibleMenu(10)
            }

            is PlayerScreenAction.EnterStringNumber -> {
                switchChannelByQuery(action.number)
            }

            PlayerScreenAction.DeleteLastNumber -> {
                deleteLastNumber()
            }

            PlayerScreenAction.GetEpgByFocusedChannelId -> {
                loadEpgByFocusedChannel()
            }

            PlayerScreenAction.DecrimentVolume -> {
                if (state.value.volume > 0f) {
                    viewModelScope.launch {
                        dataStoreRepository.saveVolume(state.value.volume - VOLUME_STEP)
                    }
                }
                timerVisibleVolumeProgress()
            }

            PlayerScreenAction.IncrimentVolume -> {
                if (state.value.volume < 1f) {
                    viewModelScope.launch {
                        dataStoreRepository.saveVolume(state.value.volume + VOLUME_STEP)
                    }
                }
                timerVisibleVolumeProgress()
            }

            PlayerScreenAction.OnResetMenuTimer -> {
                menuJob?.cancel()
                _state.update { it.copy(isVisibleSideMenu = false) }
            }

            PlayerScreenAction.OnReleaseWakeLock -> serviceRepository.releaseWakeLock()
            PlayerScreenAction.OnSetWakeLock -> acquireWakeLock()
        }
    }


    private fun getInitialData() {
        val channelId = savedStateHandle.toRoute<NavigationRoute.Player>().channelId
        _state.update { it.copy(isLoading = true, selectedChannelId = channelId, focusedChannelId = channelId) }
        Log.d(PlayerConstants.TAG, "action 6")
        viewModelScope.launch {
            val credintial = dataStoreRepository.getCredintial()
            _state.update { it.copy(credintial = credintial) }
            delay(300)
            credintial?.let {
                val resultTvChannelsDiff = async {
                    remoteRepository.getTvChannels(
                        username = credintial.username,
                        password = credintial.password
                    )
                }
                val resultTvEpgDiff = async {
                    remoteRepository.getEpg(
                        username = credintial.username,
                        password = credintial.password
                    )
                }

                when (val resultTvChannels = resultTvChannelsDiff.await()) {
                    is ResultWork.Error -> {
                        _state.update { it.copy(message = resultTvChannels.error.asUiText()) }
                    }

                    is ResultWork.Success -> {
                        _state.update { currentState ->
                            val updatedChannels = (resultTvChannels.data + radioCh).toImmutableList()
                            var selectedId = currentState.selectedChannelId
                            var focusedId = currentState.focusedChannelId
                            if (resultTvChannels.data.isNotEmpty() && selectedId == null) {
                                selectedId = resultTvChannels.data.first().tvgId
                                focusedId = resultTvChannels.data.first().tvgId
                            }
                            currentState.copy(
                                tvChannels = updatedChannels,
                                selectedChannelId = selectedId,
                                focusedChannelId = focusedId
                            )
                        }
                    }
                }
                when (val resultTvEpgs = resultTvEpgDiff.await()) {
                    is ResultWork.Error -> {
                        _state.update { it.copy(isLoading = false, message = resultTvEpgs.error.asUiText()) }
                    }

                    is ResultWork.Success -> {
                        _state.update { it.copy(isLoading = false, allTvEpg = resultTvEpgs.data.distinctBy { it.channelUuid }) }
                    }
                }
            }
        }
    }

    private fun timerVisibleVolumeProgress() {
        volumeJob?.cancel()
        _state.update { it.copy(isVisibleVolumeProgress = true) }
        volumeJob = viewModelScope.launch {
            delay(3000)
            _state.update { it.copy(isVisibleVolumeProgress = false) }
        }
    }

    private fun startTimerVisibleMenu(duration: Int) {
        _state.update { it.copy(isVisibleSideMenu = true) }
        menuJob?.cancel()
        menuJob = viewModelScope.launch {
            delay(duration * 1000L)
            _state.update { it.copy(isVisibleSideMenu = false) }
        }
    }

    private fun startTimerVisibleDrumMenu(duration: Int) {
        _state.update { it.copy(isVisibleDrumMenu = true) }
        drumMenuJob?.cancel()
        drumMenuJob = viewModelScope.launch {
            delay(duration * 1000L)
            _state.update { it.copy(isVisibleDrumMenu = false) }
        }
    }


    private fun switchChannelByQuery(number: Int) {
        _state.update { currentState ->
            if (currentState.channelInputQuery.length < 3) {
                currentState.copy(channelInputQuery = "${currentState.channelInputQuery}$number")
            } else {
                currentState.copy(channelInputQuery = currentState.channelInputQuery + number)
            }
        }
        if (state.value.channelInputQuery.length < 4) {
            switchTimer()
        } else {
            processSwitchChannel()
        }
    }

    private fun switchTimer() {
        queryJob?.cancel()
        queryJob = viewModelScope.launch {
            delay(3000)
            processSwitchChannel()
        }
    }

    private fun processSwitchChannel() {
        val number = state.value.channelInputQuery.toIntOrNull()
        number?.let {
            _state.update { currentState ->
                if (number in 1..currentState.tvChannels.size) {
                    Log.d(PlayerConstants.TAG, "action 8")
                    currentState.copy(
                        selectedChannelId = currentState.tvChannels[number - 1].tvgId,
                        focusedChannelId = currentState.tvChannels[number - 1].tvgId,
                        channelInputQuery = ""
                    )
                } else {
                    currentState.copy(channelInputQuery = "")
                }
            }
            saveLastChannel()
        }
    }

    private fun deleteLastNumber() {
        if (state.value.channelInputQuery.isNotEmpty()) {
            _state.update { it.copy(channelInputQuery = it.channelInputQuery.dropLast(1)) }
            switchTimer()
        }
    }


    private fun saveLastChannel() {
        viewModelScope.launch {
            delay(300)
            state.value.selectedChannelId?.let {
                state.value.selectedIndex?.let { index ->
                    val savedChannel = state.value.tvChannels[index]
                    localRepository.insertChannel(savedChannel)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getCurrentTime() {
        viewModelScope.launch {
            while (true) {
                val currentTime = Clock.System.now().epochSeconds
                _state.update { it.copy(currentTime = currentTime) }

                state.value.credintial?.let { credintial ->
                    val outdatedEpgs = state.value.allTvEpg.filter { it.stop <= currentTime }
                    if (outdatedEpgs.isNotEmpty()) {
                        val newEpgs = state.value.allTvEpg.map { epg ->
                            if (epg.stop <= currentTime) {
                                when (val result = remoteRepository.getEpgByChannelId(
                                    username = credintial.username,
                                    password = credintial.password,
                                    limit = SINGLE_LIMIT,
                                    channelId = epg.channelUuid
                                )) {
                                    is ResultWork.Error -> {
                                        _state.update { it.copy(message = result.error.asUiText()) }
                                        epg
                                    }
                                    is ResultWork.Success -> {
                                        result.data.firstOrNull() ?: epg
                                    }
                                }
                            } else epg
                        }
                        _state.update { it.copy(allTvEpg = newEpgs) }
                    }
                }
                delay(DELAY_MINUTE)
            }
        }
    }

    private fun getCurrentVolume() {
        viewModelScope.launch {
            dataStoreRepository.getFlowVolume().collect { volume ->
                _state.update { it.copy(volume = volume) }
            }
        }
    }

    private fun loadEpgByFocusedChannel() {
        state.value.credintial?.let { credintial ->
            state.value.focusedChannelId?.let { id ->
                _state.update { it.copy(isLoadingFocusedChannelEpg = true) }
                viewModelScope.launch {
                    when (val result = remoteRepository.getEpgByChannelId(
                        username = credintial.username,
                        password = credintial.password,
                        limit = CHANNEL_LIMIT,
                        channelId = id
                    )) {
                        is ResultWork.Error -> {
                            _state.update {
                                it.copy(
                                    isLoadingFocusedChannelEpg = false,
                                    message = result.error.asUiText()
                                )
                            }
                        }

                        is ResultWork.Success -> {
                            _state.update {
                                it.copy(
                                    isLoadingFocusedChannelEpg = false,
                                    focusedChannelEpg = result.data
                                        .groupBy { epg ->
                                            epg.start.convertToDate()
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun acquireWakeLock() {
        viewModelScope.launch {
            serviceRepository.setWakeLock()
        }
    }

    override fun onCleared() {
        super.onCleared()
        volumeJob?.cancel()
        menuJob?.cancel()
        queryJob?.cancel()
        serviceRepository.releaseWakeLock()
    }
}