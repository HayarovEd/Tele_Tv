package com.edurda77.impuls.tele_tv.player

import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.resources.uikit.UiText

data class PlayerScreenState(
    val credintial: Credintial? = null,
    val tvChannels: List<TvChannel> = emptyList(),
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val selectedChannelId: String? = null,
    val focusedChannelId: String? = null,
    val isVisibleTitle: Boolean = false,
    val isVisibleSideMenu: Boolean = false,
    val channelInputQuery: String = ""
) {
    private val selectedChannel =
        if (selectedChannelId != null && tvChannels.isNotEmpty()) tvChannels.first { it.tvgId == selectedChannelId } else null
    val selectedIndex = if (selectedChannel != null) tvChannels.indexOf(selectedChannel) else null
    private val focusedChannel =
        if (focusedChannelId != null && tvChannels.isNotEmpty()) tvChannels.first { it.tvgId == focusedChannelId } else null
    val focusedIndex = if (focusedChannel != null) tvChannels.indexOf(focusedChannel) else null
}