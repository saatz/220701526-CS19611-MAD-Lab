package com.example.sdcard

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    var E1: EditText? = null
    var B1: Button? = null
    var B2: Button? = null
    var B3: Button? = null
    var data: String = ""
    var filename = "my-data.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        E1 = findViewById<View>(R.id.E1) as EditText
        B1 = findViewById<View>(R.id.B1) as Button
        B2 = findViewById<View>(R.id.B2) as Button
        B3 = findViewById<View>(R.id.B3) as Button
        E1!!.hint = "Enter Some Text Here"

        B1!!.setOnClickListener {
            writeData()
        }
        B2!!.setOnClickListener {
            readData()
        }
        B3!!.setOnClickListener {
            E1!!.setText("")
        }
    }

    private fun writeData() {
        data = E1!!.text.toString()
        try {
            val fos: FileOutputStream = openFileOutput(filename, MODE_PRIVATE)
            fos.write(data!!.toByteArray())
            fos.close()
            Toast.makeText(applicationContext, "File Saved: $filename", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun readData() {
        var c: Int
        var temp = ""
        try {
            val fis: FileInputStream = openFileInput(filename)
            while ((fis.read().also { c = it }) != -1) {
                temp += c.toChar().toString()
            }
            E1!!.setText(temp)
            Toast.makeText(applicationContext, "File Read: $filename", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
