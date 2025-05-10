package com.example.alertdialogbox

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net. Uri
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {
    lateinit var ringtone: Ringtone
    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Alarm! Ringing", Toast.LENGTH_LONG).show()

        val alarmUri: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

    }

}