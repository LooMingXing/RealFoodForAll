package com.example.realfoodforall

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.realfoodforall.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DonorReportActivity : AppCompatActivity() {
    private lateinit var editTextRealName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var textViewReport: TextView
    private lateinit var reportData: StringBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_report)

        editTextRealName = findViewById(R.id.editTextRealName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPhone = findViewById(R.id.editTextPhone)
        textViewReport = findViewById(R.id.textViewReportGenerateTitle)
        reportData = StringBuilder()

        val buttonGenerateReport = findViewById<Button>(R.id.buttonGenerateReport)
        buttonGenerateReport.setOnClickListener {
            val realName = editTextRealName.text.toString()
            val email = editTextEmail.text.toString()
            val phone = editTextPhone.text.toString()

            // Generate and display the report data
            generateReportData(realName, email, phone)

            // Generate and share the PDF report
            generateAndSharePdfReport()
        }
    }

    private fun generateReportData(realName: String, email: String, phone: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("FoodDonation/011020233250")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Build the report data based on Firebase data
                    reportData.apply {
                        append("Donated Items\n\n") // Updated report title
                        append("Real Name: $realName\n")
                        append("Email: $email\n")
                        append("Phone: $phone\n\n")
                        append("Firebase Data:\n")

                        for (child in dataSnapshot.children) {
                            val key = child.key
                            val value = child.value
                            append("$key: $value\n")
                        }
                    }

                    // Display the report data on the screen
                    textViewReport.text = reportData.toString()

                    // Generate and share the PDF report
                    generateAndSharePdfReport()
                } else {
                    // Handle the case when Firebase data does not exist
                    Toast.makeText(
                        this@DonorReportActivity,
                        "No data found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@DonorReportActivity, "Database error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun generateAndSharePdfReport() {
        try {
            val directory = File(filesDir, "")
            Log.d("File Path", "Directory path: ${directory.absolutePath}")

            // Create a PDF document
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Customize the PDF appearance
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 12f

            // Split the report data into lines for formatting
            val lines = reportData.toString().split("\n")

            var y = 10f
            for (line in lines) {
                canvas.drawText(line, 10f, y, paint)
                y += 20f // Adjust the line spacing
            }

            pdfDocument.finishPage(page)

            // Save the PDF file (You may need to request storage permissions)
            val pdfFile = File(filesDir, "report.pdf")

            // After PDF generation is complete, open the ReportActivity
            val intent = Intent(this@DonorReportActivity, ReportActivity::class.java)
            intent.putExtra("reportContent", reportData.toString())
            startActivity(intent)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "PDF generation failed: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

}