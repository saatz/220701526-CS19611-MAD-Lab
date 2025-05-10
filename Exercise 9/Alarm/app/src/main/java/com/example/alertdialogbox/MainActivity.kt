package com.example.alertdialogbox

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.widget. TimePicker
import java.util .*

class MainActivity : AppCompatActivity() {

    private lateinit var alarmTimePicker: TimePicker
    private lateinit var pendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSet = findViewById<Button>(R.id.btnSet)
        val btnStop = findViewById<Button>(R.id.btnStop)
        alarmTimePicker = findViewById(R.id.timePicker)
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        btnSet.setOnClickListener {
            Toast.makeText(this, "ALARM ON", Toast.LENGTH_SHORT).show()
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.hour)
            calendar.set(Calendar.MINUTE, alarmTimePicker.minute)
            val intent = Intent(this, AlarmReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(
                this.applicationContext, 2, intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val time: Long = calendar.timeInMillis - (calendar.timeInMillis % 60000)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, time, 10000,
                pendingIntent
            )

            btnStop.setOnClickListener {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "ALARM OFF", Toast.LENGTH_SHORT).show()
            }
        }
    }
}