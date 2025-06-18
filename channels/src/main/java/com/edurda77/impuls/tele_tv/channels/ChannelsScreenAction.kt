package com.edurda77.impuls.tele_tv.channels

sealed interface ChannelsScreenAction {
    class UpdateFocusedIndex(val index: Int) : ChannelsScreenAction
    data object SaveSelectedChannel : ChannelsScreenAction
    data object DownloadUpdate : ChannelsScreenAction
}