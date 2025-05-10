package com.example.sendemail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail : EditText = findViewById(R.id.etEmail)
        val etSubject : EditText = findViewById(R.id.etSubject)
        val etMessage : EditText = findViewById(R.id.etMessage)
        val btSend : Button = findViewById(R.id.btSend)

        btSend.setOnClickListener {
            val email = etEmail.text.toString()
            val subject = etSubject.text.toString()
            val message = etMessage.text.toString()
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "message/rfc822"
            startActivity(Intent.createChooser(intent, "Choose an Email client:"))
        }
    }
}