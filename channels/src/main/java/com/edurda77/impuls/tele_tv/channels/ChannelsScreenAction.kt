package com.edurda77.impuls.tele_tv.channels

import com.edurda77.impuls.tele_tv.domain.model.Category
import com.edurda77.impuls.tele_tv.domain.model.TvChannel

sealed interface ChannelsScreenAction {
    class SaveChosenChannel(val channel: TvChannel) : ChannelsScreenAction
    data object DownloadUpdate : ChannelsScreenAction
    data object Logout : ChannelsScreenAction
    class UpdateSelectedTabIndex(val category: Category): ChannelsScreenAction
}