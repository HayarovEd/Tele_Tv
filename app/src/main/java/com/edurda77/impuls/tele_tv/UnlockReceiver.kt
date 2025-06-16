package com.edurda77.impuls.tele_tv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.edurda77.impuls.tele_tv.domain.utils.IS_SCREEN_ON

class UnlockReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_ON) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            launchIntent.putExtra(IS_SCREEN_ON, true)
            context?.startActivity(launchIntent)
        }
    }
}