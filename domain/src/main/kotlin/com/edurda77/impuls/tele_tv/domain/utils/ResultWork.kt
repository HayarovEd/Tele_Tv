package com.edurda77.impuls.tele_tv.domain.utils


sealed interface ResultWork<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): ResultWork<D, E>
    data class Error<out D, out E: RootError>(val error: E): ResultWork<D, E>

}