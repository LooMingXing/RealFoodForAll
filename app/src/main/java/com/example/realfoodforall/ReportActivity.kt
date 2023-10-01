package com.example.realfoodforall

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val textViewReport = findViewById<TextView>(R.id.textViewReportContent)

        // Retrieve the report data from the intent
        val reportContent = intent.getStringExtra("reportContent")

        // Display the report data in the TextView
        textViewReport.text = reportContent
    }
}