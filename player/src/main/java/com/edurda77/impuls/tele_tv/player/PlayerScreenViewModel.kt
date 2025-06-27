package com.edurda77.impuls.tele_tv.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edurda77.impuls.tele_tv.domain.repository.DataStoreRepository
import com.edurda77.impuls.tele_tv.domain.repository.LocalRepository
import com.edurda77.impuls.tele_tv.domain.repository.RemoteRepository
import com.edurda77.impuls.tele_tv.domain.utils.CHANNEL_LIMIT
import com.edurda77.impuls.tele_tv.domain.utils.DELAY_MINUTE
import com.edurda77.impuls.tele_tv.domain.utils.ResultWork
import com.edurda77.impuls.tele_tv.domain.utils.SINGLE_LIMIT
import com.edurda77.impuls.tele_tv.domain.utils.convertToDate
import com.edurda77.impuls.tele_tv.resources.uikit.asUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PlayerScreenViewModel(
    private val remoteRepository: RemoteRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val localRepository: LocalRepository,
) : ViewModel() {


    private var job: Job? = null
    private var inputJob: Job? = null
    private var menuJob: Job? = null
    private var queryJob: Job? = null

    private val _state = MutableStateFlow(PlayerScreenState())
    val state = _state
        .onStart {
            getInitialData()
            getCurrentTime()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = PlayerScreenState()
        )

    fun onAction(action: PlayerScreenAction) {
        when (action) {
            PlayerScreenAction.OnShowTitle -> {
                startTimer()
            }

            PlayerScreenAction.DecrimentTvChannel -> {
                state.value.selectedIndex?.let { selectedIndex ->
                    if (state.value.tvChannels[selectedIndex] == state.value.tvChannels.first()) {
                        _state.value.copy(
                            selectedChannelId = state.value.tvChannels.last().tvgId,
                            focusedChannelId = state.value.tvChannels.last().tvgId,
                        )
                            .updateState()
                    } else {
                        _state.value.copy(
                            selectedChannelId = state.value.tvChannels[selectedIndex - 1].tvgId,
                            focusedChannelId = state.value.tvChannels[selectedIndex - 1].tvgId,
                        )
                            .updateState()
                    }
                    saveLastChannel()
                    startTimer()
                }
            }

            PlayerScreenAction.IncrimentTvChannel -> {
                state.value.selectedIndex?.let { selectedIndex ->
                    if (state.value.tvChannels[selectedIndex] == state.value.tvChannels.last()) {
                        _state.value.copy(
                            selectedChannelId = state.value.tvChannels.first().tvgId,
                            focusedChannelId = state.value.tvChannels.first().tvgId
                        )
                            .updateState()
                    } else {
                        _state.value.copy(
                            selectedChannelId = state.value.tvChannels[selectedIndex + 1].tvgId,
                            focusedChannelId = state.value.tvChannels[selectedIndex + 1].tvgId,
                        )
                            .updateState()
                    }
                    saveLastChannel()
                    startTimer()
                }
            }

            is PlayerScreenAction.UpdateSelectedIndex -> {
                _state.value.copy(
                    selectedChannelId = state.value.focusedChannelId
                )
                    .updateState()
                saveLastChannel()
                startTimerVisibleMenu()
            }

            PlayerScreenAction.DecrimentFocusedIndex -> {
                state.value.focusedIndex?.let { focusedIndex ->
                    Log.d("REST TELE TV", "focusedIndex vm increment $focusedIndex")
                    if (state.value.tvChannels[focusedIndex] == state.value.tvChannels.first()) {
                        _state.value.copy(
                            focusedChannelId = state.value.tvChannels.last().tvgId
                        )
                            .updateState()
                    } else {
                        _state.value.copy(
                            focusedChannelId = state.value.tvChannels[focusedIndex - 1].tvgId
                        )
                            .updateState()
                    }
                    loadEpgByFocusedChannel()
                    startTimerVisibleMenu()
                }
            }

            PlayerScreenAction.IncrimentFocusedIndex -> {
                state.value.focusedIndex?.let { focusedIndex ->
                    Log.d("REST TELE TV", "focusedIndex vm decrement $focusedIndex")
                    if (state.value.tvChannels[focusedIndex] == state.value.tvChannels.last()) {
                        _state.value.copy(
                            focusedChannelId = state.value.tvChannels.first().tvgId
                        )
                            .updateState()
                    } else {
                        _state.value.copy(
                            focusedChannelId = state.value.tvChannels[focusedIndex + 1].tvgId
                        )
                            .updateState()
                    }
                    loadEpgByFocusedChannel()
                    startTimerVisibleMenu()
                }
            }

            PlayerScreenAction.ShowSideMenu -> {
                _state.value.copy(
                    isVisibleSideMenu = true
                )
                    .updateState()
                startTimerVisibleMenu()
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
                    selectedChannelId = it,
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
                        _state.value.copy(
                            message = resultTvChannels.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            tvChannels = resultTvChannels.data
                        )
                            .updateState()
                        if (resultTvChannels.data.isNotEmpty() && state.value.selectedChannelId == null) {
                            _state.value.copy(
                                selectedChannelId = resultTvChannels.data.first().tvgId,
                                focusedChannelId = resultTvChannels.data.first().tvgId,
                            )
                                .updateState()

                            startTimer()
                        }
                    }
                }
                when (val resultTvEpgs = resultTvEpgDiff.await()) {
                    is ResultWork.Error -> {
                        _state.value.copy(
                            isLoading = false,
                            message = resultTvEpgs.error.asUiText()
                        )
                            .updateState()
                    }

                    is ResultWork.Success -> {
                        _state.value.copy(
                            isLoading = false,
                            allTvEpg = resultTvEpgs.data.distinctBy { it.channelUuid }
                        )
                            .updateState()
                    }
                }
            }
        }
    }

    private fun startTimer() {
        job?.cancel()
        job = viewModelScope.launch {
            _state.value.copy(
                isVisibleTitle = true
            )
                .updateState()
            (4 downTo 0).forEach { _ ->
                delay(1000)
            }
            _state.value.copy(
                isVisibleTitle = false
            )
                .updateState()
        }
        job?.start()
    }

    private fun startTimerVisibleMenu() {
        menuJob?.cancel()
        menuJob = viewModelScope.launch {
            (2 downTo 0).forEach { _ ->
                delay(1000)
            }
            _state.value.copy(
                isVisibleSideMenu = false
            )
                .updateState()
        }
        menuJob?.start()
    }


    private fun switchChannelByQuery(number: Int) {
        if (state.value.channelInputQuery.length < 3) {
            _state.value.copy(
                channelInputQuery = "${state.value.channelInputQuery}$number"
            )
                .updateState()
            switchTimer()
        } else {
            _state.value.copy(
                channelInputQuery = state.value.channelInputQuery + number
            )
                .updateState()
            // delay(300)
            processSwitchChannel()
        }
    }

    private fun switchTimer() {
        queryJob?.cancel()
        queryJob = viewModelScope.launch {
            (3 downTo 0).forEach { _ ->
                delay(1000)
            }
            Log.d("REST TELE TV", "channelInputQuery ${state.value.channelInputQuery}")
            processSwitchChannel()
        }
        queryJob?.start()
    }

    private fun processSwitchChannel() {
        val number = state.value.channelInputQuery.toIntOrNull()
        number?.let {
            if (number in 1..<state.value.tvChannels.size) {
                _state.value.copy(
                    selectedChannelId = state.value.tvChannels[number - 1].tvgId,
                    focusedChannelId = state.value.tvChannels[number - 1].tvgId,
                    channelInputQuery = ""
                )
                    .updateState()
                saveLastChannel()
                startTimer()
            } else {
                _state.value.copy(
                    channelInputQuery = ""
                )
                    .updateState()
            }
        }
    }

    private fun deleteLastNumber() {
        if (state.value.channelInputQuery.isNotEmpty()) {
            _state.value.copy(
                channelInputQuery = state.value.channelInputQuery.dropLast(1)
            )
                .updateState()
            switchTimer()
        }
    }


    private fun saveLastChannel() {
        viewModelScope.launch {
            delay(300)
            state.value.selectedChannelId?.let {
                dataStoreRepository.saveLastChannel(it)
                state.value.selectedIndex?.let { index ->
                    val savedChannel = state.value.tvChannels[index]
                    localRepository.insertLocation(savedChannel)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun getCurrentTime() {
        viewModelScope.launch {
            while (true) {
                delay(DELAY_MINUTE)
                val currentTime = Clock.System.now().epochSeconds
                state.value.credintial?.let { credintial ->
                    val newEpgs = state.value.allTvEpg.map { epg ->
                        if (epg.stop > currentTime) {
                            when (val result = remoteRepository.getEpgByChannelId(
                                username = credintial.username,
                                password = credintial.password,
                                limit = SINGLE_LIMIT,
                                channelId = epg.channelUuid
                            )) {
                                is ResultWork.Error -> {
                                    _state.value.copy(
                                        message = result.error.asUiText()
                                    )
                                        .updateState()
                                    epg
                                }

                                is ResultWork.Success -> {
                                    if (result.data.isEmpty()) epg else
                                        result.data.first()
                                }
                            }
                        } else epg
                    }
                    _state.value.copy(
                        currentTime = currentTime,
                        allTvEpg = newEpgs
                    )
                        .updateState()
                }
            }
        }
    }

    private fun loadEpgByFocusedChannel() {
        state.value.credintial?.let { credintial ->
            state.value.focusedChannelId?.let { id ->
                _state.value.copy(
                    isLoadingFocusedChannelEpg = true
                )
                    .updateState()
                viewModelScope.launch {
                    when (val result = remoteRepository.getEpgByChannelId(
                        username = credintial.username,
                        password = credintial.password,
                        limit = CHANNEL_LIMIT,
                        channelId = id
                    )) {
                        is ResultWork.Error -> {
                            _state.value.copy(
                                isLoadingFocusedChannelEpg = false,
                                message = result.error.asUiText()
                            )
                                .updateState()
                        }

                        is ResultWork.Success -> {
                            _state.value.copy(
                                isLoadingFocusedChannelEpg = false,
                                focusedChannelEpg = result.data
                                    .groupBy {
                                        it.start.convertToDate()
                                    }
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

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        inputJob?.cancel()
        menuJob?.cancel()
        queryJob?.cancel()
    }
}