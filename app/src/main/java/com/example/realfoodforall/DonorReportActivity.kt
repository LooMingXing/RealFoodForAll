package com.example.realfoodforall

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DonorReportActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var editTextRealName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_report)

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference.child("FoodDonation")

        editTextRealName = findViewById(R.id.editTextRealName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPhone = findViewById(R.id.editTextPhone)

        val buttonGenerateReport = findViewById<Button>(R.id.buttonGenerateReport)
        buttonGenerateReport.setOnClickListener {
            val realName = editTextRealName.text.toString()
            val email = editTextEmail.text.toString()
            val phone = editTextPhone.text.toString()

            fetchDataAndGenerateReport(realName, email, phone)
        }
    }

    private fun fetchDataAndGenerateReport(realName: String, email: String, phone: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val pdfDocument = PdfDocument()
                    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas

                    val reportText =
                        "Report Title\n\n" +
                                "Real Name: $realName\n" +
                                "Email: $email\n" +
                                "Phone: $phone\n\n"


                    val data = dataSnapshot.getValue(String::class.java)

                    canvas.drawText(reportText, 10f, 10f, Paint(Color.rgb(0,0,0)))


                    pdfDocument.finishPage(page)

                    val pdfFile = File(
                        Environment.getExternalStorageDirectory(),
                        "report.pdf"
                    )

                    try {
                        val fileOutputStream = FileOutputStream(pdfFile)
                        pdfDocument.writeTo(fileOutputStream)
                        pdfDocument.close()
                        fileOutputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}