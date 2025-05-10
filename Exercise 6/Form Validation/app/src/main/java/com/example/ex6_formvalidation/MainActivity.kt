package com.example.ex6_formvalidation

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val etUserName : EditText=findViewById(R.id.etUserName)
        val etPinNumber: EditText =findViewById(R.id.etPinNumber)
        val btLogin: Button =findViewById(R.id.btLogin)
        val btClear:Button=findViewById(R.id.btClear)

        btLogin.setOnClickListener{
            val userName=etUserName.text.toString().trim()
            val pinNumber=etPinNumber.text.toString().trim()
            if(userName.isEmpty() || pinNumber.isEmpty())
            {
                Toast.makeText(this,"All field are mandatory",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(!userName.matches(Regex("[a-zA-Z]+$")))
            {
                Toast.makeText(this,"Invalid Username",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(!pinNumber.matches(Regex("^[0-9]{4}$")))
            {
                Toast.makeText(this,"Invalid Pin Number",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val intent: Intent =Intent(this,MainActivity2::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}