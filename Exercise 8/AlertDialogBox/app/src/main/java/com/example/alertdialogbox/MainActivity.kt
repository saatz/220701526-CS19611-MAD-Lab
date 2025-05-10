package com.example.alertdialogbox

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.view.View
import android.content.Intent
import android.app.AlertDialog
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alert = findViewById<Button>(R.id.alert_Button)
        val clear = findViewById<Button>(R.id.clear_Button)
        val sms = findViewById<EditText>(R.id.edit_Text)
        val builder = AlertDialog.Builder(this)

        alert.setOnClickListener {
            val message = sms.text.toString()
            if (message != "") {
                builder.setMessage(message).setTitle("New Message")
                builder.setMessage(message)
                builder.setCancelable(false)
                builder.setPositiveButton("OK") { dialogInterface, which ->
                    Toast.makeText(applicationContext, "AlertDialog Closed", Toast.LENGTH_LONG).show()
                }

                val smsIntent = Intent(this, SmsAlert::class.java)
                smsIntent.putExtra(message, 1)
                val alertBox: AlertDialog = builder.create()
                alertBox.setTitle("New Message")
                alertBox.show()
                startActivity(smsIntent)
            } else {
                Toast.makeText(applicationContext, "Type Message in Message Box", Toast.LENGTH_LONG).show()
            }
        }

        clear.setOnClickListener {
            sms.setText("")
        }
    }
}