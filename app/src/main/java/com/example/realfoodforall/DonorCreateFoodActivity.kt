package com.example.realfoodforall

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.realfoodforall.databinding.ActivityDonorBinding
import com.example.realfoodforall.databinding.ActivityDonorCreateFoodBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DonorCreateFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorCreateFoodBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var mStorageRef: StorageReference
    private lateinit var imageUri : Uri

//    For TimePicker
    private var selectedFromTime: String = ""
    private var selectedToTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorCreateFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase?.reference!!.child("FoodDonation")

//        For Firebase storage image
        mStorageRef = FirebaseStorage.getInstance().getReference("Images")

//        For Progress Bar
        binding.progressBarUpload.visibility = View.GONE
        binding.textViewProgress.visibility = View.GONE


        setSupportActionBar(binding.toolbarDonorCreateFood)
        supportActionBar!!.title = "Donor Create Food"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


//        For fetching the image uri!
        val pickImageFromGalleryForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imageViewFoodImage.setImageURI(it)

                if (it != null) {
                    imageUri = it
                }
            }

        // for open gallery to choose image
        binding.buttonChooseImage.setOnClickListener {
            pickImageFromGalleryForResult.launch("image/*")
        }

//        For TimePicker
        binding.editTextFromTime.setOnClickListener {
            showTimePickerDialog(true)
        }

        binding.editTextToTime.setOnClickListener {
            showTimePickerDialog(false)
        }

        binding.buttonConfirmDonation.setOnClickListener {
            val food_name = binding.editTextFoodName.text.toString()
            val food_portion = binding.editTextFoodPortion.text.toString()
            val food_address = binding.editTextFoodAddress.text.toString()
            val food_date = binding.editTextFoodDate.text.toString()
            var food_to_time = binding.editTextToTime.text.toString()
            val food_from_time = binding.editTextFromTime.text.toString()

            if (food_name.isEmpty()) {
                binding.editTextFoodName.error = "Please enter food name!"
            }

            if (food_portion.isEmpty()) {
                binding.editTextFoodPortion.error = "Must enter food portion number!"
            }

//            Remember to add Geocoder function to validate the address!!
            if (food_address.isEmpty()) {
                binding.editTextFoodAddress.error = "Must enter the location!!"
            }

            if (food_date.isEmpty()) {
                binding.editTextFoodDate.error = "Must enter the food date!!"
            }

            if (!isDateValid(food_date)) {
                binding.editTextFoodDate.error = "Date is not valid!!"
                return@setOnClickListener
            }

            if (food_to_time.isEmpty() || food_from_time.isEmpty()) {
                binding.editTextToTime.error = "Must enter to time!"
                binding.editTextFromTime.error = "Must Enter From Time!"
            }

            addFoodToDb(
                binding.editTextFoodName.text.toString(),
                binding.editTextFoodPortion.text.toString(),
                binding.editTextFoodAddress.text.toString(),
                binding.editTextFoodDate.text.toString(),
                binding.editTextFromTime.text.toString(),
                binding.editTextToTime.text.toString()
            )
        }

    }

    private fun isDateValid(dateStr: String): Boolean {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.parse(dateStr)
            val currentDate = Calendar.getInstance().time

            // Compare the selected date with the current date
            return !selectedDate.before(currentDate)

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun addFoodToDb(
        foodName: String,
        foodPortion: String,
        foodLocation: String,
        foodDate: String,
        foodFromTime: String,
        foodToTime: String
    ) {

        val currentUser = mAuth.currentUser
        val foodDonationId = SimpleDateFormat("ddMMyyyy",Locale.getDefault()).format(Date()) + (1000..9999).random()

        imageUri?.let {
            binding.progressBarUpload.visibility = View.VISIBLE
            binding.textViewProgress.visibility = View.VISIBLE

            mStorageRef.child(foodDonationId).putFile(it)
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                    binding.progressBarUpload.progress = progress
                    binding.textViewProgress.text = "$progress %"
                }

                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgUrl = url.toString()

                            val addToDatabase = mRef.child(foodDonationId)
                            addToDatabase.child("foodDonorUID").setValue(currentUser?.uid!!)
                            addToDatabase.child("foodName").setValue(foodName)
                            addToDatabase.child("foodPortion").setValue(foodPortion)
                            addToDatabase.child("foodLocation").setValue(foodLocation)
                            addToDatabase.child("foodDate").setValue(foodDate)
                            addToDatabase.child("foodFromTime").setValue(foodFromTime)
                            addToDatabase.child("foodToTime").setValue(foodToTime)
                            addToDatabase.child("foodURL").setValue(imgUrl)

                            Toast.makeText(this,"Data Stored Successfully",Toast.LENGTH_LONG).show()

                            val intent = Intent(this@DonorCreateFoodActivity, DonorActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Data failed to Store", Toast.LENGTH_LONG)
                                .show()
                        }

                }
        }

    }

    private fun showTimePickerDialog(isFromTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val selectedHour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                val selectedMinute = if (minute < 10) "0$minute" else minute.toString()
                val selectedTime = "$selectedHour:$selectedMinute"

                if (isFromTime) {
                    selectedFromTime = selectedTime
                    binding.editTextFromTime.setText(selectedFromTime)
                } else {
                    // Check if ToTime is not earlier than FromTime
                    if (isToTimeValid(selectedTime, selectedFromTime)) {
                        selectedToTime = selectedTime
                        binding.editTextToTime.setText(selectedToTime)
                    } else {
                        Toast.makeText(
                            this,
                            "To Time cannot be earlier than From Time!!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            hourOfDay,
            minute,
            false // Set 24-hour format to false to display AM/PM
        )

        timePickerDialog.show()
    }

    private fun isToTimeValid(toTime: String, fromTime: String): Boolean {
        val sdf = SimpleDateFormat("hh:mm", Locale.getDefault())
        val toTimeDate = sdf.parse(toTime)
        val fromTimeDate = sdf.parse(fromTime)

        return !toTimeDate.before(fromTimeDate)
    }
}