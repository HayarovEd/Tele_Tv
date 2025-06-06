package com.edurda77.impuls.tele_tv.login

import com.edurda77.resources.uikit.UiText


data class LoginScreenState(
    val message: UiText? = null,
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)