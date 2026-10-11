package com.edurda77.impuls.tele_tv.player

import com.edurda77.resources.uikit.UiText

sealed interface PlayerScreenEvent {
    class UiError(val message: UiText) : PlayerScreenEvent
}