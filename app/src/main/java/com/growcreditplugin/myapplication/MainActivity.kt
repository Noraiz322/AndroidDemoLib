package com.growcreditplugin.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.growcreditplugin.sumlib.SumValue

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.tvSum) // Initialize TextView reference

        // Now you can use textView as needed, for example:
        textView.text = "Hello, TextView!" + SumValue(5, 6)


    }
}