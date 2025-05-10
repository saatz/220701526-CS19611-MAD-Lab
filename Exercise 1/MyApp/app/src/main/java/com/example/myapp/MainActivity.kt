package com.example.myapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.graphics.Typeface
import android.graphics.Color
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapp.ui.theme.MyAppTheme

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val t1 = findViewById<TextView>(R.id.text1)

        val b1 = findViewById<Button>(R.id.font1)
        val b2 = findViewById<Button>(R.id.color1)

        var fontIndex = 0
        val fonts = arrayOf(Typeface.DEFAULT_BOLD, Typeface.MONOSPACE,Typeface.DEFAULT_BOLD)
        var currentIndex = 0
        val fontSizes = arrayOf(16f, 20f, 24f, 28f, 32f)

        var colorIndex = 0
        val colors = arrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.BLACK)


        b1.setOnClickListener {
            fontIndex = (fontIndex + 1) % fonts.size
            t1.typeface = fonts[fontIndex]
            currentIndex = (currentIndex + 1) % fontSizes.size
            t1.textSize = fontSizes[currentIndex]
            //t1.textSize = 30F
            Toast.makeText(this, "Font and Size Changed", Toast.LENGTH_SHORT).show()
        }

        b2.setOnClickListener {
            colorIndex = (colorIndex + 1) % colors.size
            t1.setTextColor(colors[colorIndex])

            //t2.setTextColor(Color.RED)
            Toast.makeText(this,"Font Color Changed",Toast.LENGTH_SHORT).show()
        }

    }
}