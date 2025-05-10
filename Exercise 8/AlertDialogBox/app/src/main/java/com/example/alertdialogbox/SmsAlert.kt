package com.example.alertdialogbox

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class SmsAlert : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_alert)

        val showmsg = findViewById<TextView>(R.id.showmsg)
        val extras = intent.extras
        showmsg.text = extras!!.getString("sms")
    }
}