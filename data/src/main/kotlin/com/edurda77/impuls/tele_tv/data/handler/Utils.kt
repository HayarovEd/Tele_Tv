package com.edurda77.impuls.tele_tv.data.handler

import android.content.Context

fun Context.pathToDownloadFile(fileName: String): String =
    "${externalCacheDir?.absolutePath}/$fileName"