package com.edurda77.impuls.tele_tv.player

import com.edurda77.impuls.tele_tv.domain.model.Credintial
import com.edurda77.impuls.tele_tv.domain.model.TvChannel
import com.edurda77.resources.uikit.UiText

data class PlayerScreenState(
    val credintial: Credintial? =null,
    val tvChannels: List<TvChannel> = emptyList(),
    val isLoading: Boolean = false,
    val message: UiText? = null,
)