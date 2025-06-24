package com.edurda77.impuls.tele_tv.channels

import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.model.LastVersionApp
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.resources.uikit.UiText

data class ChannelsScreenState(
    val credintial: Credintial? = null,
    val tvChannels: List<TvChannel> = emptyList(),
    val lastTvChannels: List<TvChannel> = emptyList(),
    val isLoading: Boolean = false,
    val message: UiText? = null,
    val focusedChannelId: String? = null,
    val enableUpdate: Boolean = false,
    val isUpdating: Boolean = false,
    val release: LastVersionApp? = null,
    val percentDownload: Int = 0,
) {
    private val scrolledChannel = if (focusedChannelId != null&&tvChannels.isNotEmpty())
        tvChannels.first { it.tvgId == focusedChannelId } else null
    val scrolledIndex = if (scrolledChannel != null)
        tvChannels.indexOf(scrolledChannel) else null
}