package com.edurda77.impuls.tele_tv.player


sealed interface PlayerScreenAction {
    class OnRestartMenuTimer(val duration: Int):PlayerScreenAction
    data object OnResetMenuTimer:PlayerScreenAction
    data object DecrimentTvChannel:PlayerScreenAction
    data object IncrimentTvChannel:PlayerScreenAction
    data object DecrimentFocusedIndex:PlayerScreenAction
    data object IncrimentFocusedIndex:PlayerScreenAction
    data object UpdateSelectedIndex:PlayerScreenAction
    data object ShowSideMenu:PlayerScreenAction
    class EnterStringNumber(val number: Int):PlayerScreenAction
    data object DeleteLastNumber:PlayerScreenAction
    data object GetEpgByFocusedChannelId:PlayerScreenAction
    data object DecrimentVolume:PlayerScreenAction
    data object IncrimentVolume:PlayerScreenAction
}