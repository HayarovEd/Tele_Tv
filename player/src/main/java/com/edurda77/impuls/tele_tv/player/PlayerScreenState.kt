package com.edurda77.impuls.tele_tv.player


import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.impuls.tele_tv.domain.model.TvEpg
import com.edurda77.resources.uikit.UiText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class PlayerScreenState @OptIn(ExperimentalTime::class) constructor(
    val credintial: Credintial? = null,
    val tvChannels: ImmutableList<TvChannel> = persistentListOf(),
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val selectedChannelId: String? = null,
    val focusedChannelId: String? = null,
    val isVisibleTitle: Boolean = false,
    val isVisibleSideMenu: Boolean = false,
    val channelInputQuery: String = "",
    val allTvEpg: List<TvEpg> = emptyList(),
    val currentTime: Long = Clock.System.now().epochSeconds,
    val focusedChannelEpg: Map<LocalDate, List<TvEpg>> = emptyMap(),
    val isLoadingFocusedChannelEpg: Boolean = false,
    val volume: Float = 0.5f,
    val isVisibleVolumeProgress: Boolean = false,
) {
    private val selectedChannel =
        if (selectedChannelId != null && tvChannels.isNotEmpty()) tvChannels.first { it.tvgId == selectedChannelId } else null
    val selectedIndex = if (selectedChannel != null) tvChannels.indexOf(selectedChannel) else null
    private val focusedChannel =
        if (focusedChannelId != null && tvChannels.isNotEmpty()) tvChannels.first { it.tvgId == focusedChannelId } else null
    val focusedIndex = if (focusedChannel != null) tvChannels.indexOf(focusedChannel) else null

    val grouppedEpg = allTvEpg.associateBy { it.channelUuid }

}