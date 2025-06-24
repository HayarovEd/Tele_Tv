package com.edurda77.impuls.tele_tv.channels

sealed interface ChannelsScreenAction {
    class UpdateFocusedIndex(val id: String) : ChannelsScreenAction
    data object SaveSelectedChannel : ChannelsScreenAction
    data object DownloadUpdate : ChannelsScreenAction
}