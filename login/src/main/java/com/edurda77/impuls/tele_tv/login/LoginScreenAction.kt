package com.edurda77.impuls.tele_tv.login

sealed interface LoginScreenAction {
    class OnSetUsername(val username: String) : LoginScreenAction
    class OnSetPassword(val password: String) : LoginScreenAction
    data object OnLogin : LoginScreenAction
}