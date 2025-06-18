package com.edurda77.impuls.tele_tv.player

sealed interface PlayerScreenAction {
    data object OnShowTitle:PlayerScreenAction
    data object DecrimentTvChannel:PlayerScreenAction
    data object IncrimentTvChannel:PlayerScreenAction
    data object DecrimentFocusedIndex:PlayerScreenAction
    data object IncrimentFocusedIndex:PlayerScreenAction
    data object UpdateSelectedIndex:PlayerScreenAction
    data object ShowSideMenu:PlayerScreenAction
}