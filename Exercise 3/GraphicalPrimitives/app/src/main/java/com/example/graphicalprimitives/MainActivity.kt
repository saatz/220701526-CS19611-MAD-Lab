package com.example.graphicalprimitives

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tl = findViewById<TouchScreen>(R.id.t1) // Fixed `R_id` to `R.id`
        val bl = findViewById<Button>(R.id.b1)
        val b2 = findViewById<Button>(R.id.b2)
        val b3 = findViewById<Button>(R.id.b3)
        val b4 = findViewById<Button>(R.id.b4)

        bl.setOnClickListener {
            tl.setDrawingMode(0) // Fixed method name from `setDrawint` to `setDrawingMode`
        }

        b2.setOnClickListener {
            tl.setDrawingMode(1) // Fixed `l` (which was undefined) to `1`
        }

        b3.setOnClickListener {
            tl.setDrawingMode(2) // Fixed `t1` (which was undefined) to `tl`
        }

        b4.setOnClickListener {
            tl.startDrawing() // Fixed `t1` (which was undefined) to `tl`
        }
    }
}
