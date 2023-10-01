package com.example.realfoodforall

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.realfoodforall.databinding.ActivityDonorUpdateFoodBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DonorUpdateFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorUpdateFoodBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var mStorageRef: StorageReference

//    Must put his imageUri as the below format, cannot put as lateinit !!
    private var imageUri : Uri? = null

    private lateinit var realFoodDonationId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorUpdateFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase?.reference!!.child("FoodDonation")

//        For Firebase storage image
        mStorageRef = FirebaseStorage.getInstance().getReference("Images")

        val bundle = intent.extras
        val foodDonationId = bundle?.getString("Food Donation Id")
        val foodImageUrl = bundle?.getString("Image")
        val foodName = bundle?.getString("Food Name")
        val foodPortion = bundle?.getString("Food Portion")
        val foodDate = bundle?.getString("Food Date")
        val foodLocation = bundle?.getString("Food Location")
        val foodToTime = bundle?.getString("Food ToTime")
        val foodFromTime = bundle?.getString("Food FromTime")

        realFoodDonationId = foodDonationId.toString()

        Glide.with(this)
            .load(foodImageUrl)
            .into(binding.imageViewUpdateFoodImage)

        setSupportActionBar(binding.toolbarDonorUpdateFood)
        supportActionBar!!.title = "Donor Update Food"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.editTextUpdateFoodName.setText(foodName)
        binding.editTextUpdateFoodAddress.setText(foodLocation)
        binding.editTextUpdateFoodDate.setText(foodDate)
        binding.editTextUpdateFoodPortion.setText(foodPortion)
        binding.editTextUpdateToTime.setText(foodToTime)
        binding.editTextUpdateFromTime.setText(foodFromTime)

        //        For fetching the image uri!
        val pickImageFromGalleryForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imageViewUpdateFoodImage.setImageURI(it)

                if (it != null) {
                    imageUri = it
                }
            }

        // for open gallery to choose image
        binding.buttonUpdateImage.setOnClickListener {
            pickImageFromGalleryForResult.launch("image/*")
        }

        binding.buttonUpdateFoodDonation.setOnClickListener {
            val updatedFoodName = binding.editTextUpdateFoodName.text.toString()
            val updatedFoodPortion = binding.editTextUpdateFoodPortion.text.toString()
            val updatedFoodDate = binding.editTextUpdateFoodDate.text.toString()
            val updatedFoodLocation = binding.editTextUpdateFoodAddress.text.toString()
            val updatedFoodToTime = binding.editTextUpdateToTime.text.toString()
            val updatedFoodFromTime = binding.editTextUpdateFromTime.text.toString()

            if (updatedFoodName.isEmpty()) {
                binding.editTextUpdateFoodName.error = "Food name cannot be empty"
            }

            if (updatedFoodPortion.isEmpty()) {
                binding.editTextUpdateFoodPortion.error = "Food portion cannot be empty"
            }

            if (updatedFoodDate.isEmpty()) {
                binding.editTextUpdateFoodDate.error = "Food date cannot be empty"
            }

            if (updatedFoodLocation.isEmpty()) {
                binding.editTextUpdateFoodAddress.error = "Food location cannot be empty"
            }

            if (updatedFoodToTime.isEmpty()) {
                binding.editTextUpdateToTime.error = "Food to time cannot be empty"
            }

            if (updatedFoodFromTime.isEmpty()) {
                binding.editTextUpdateFromTime.error = "Food from time cannot be empty"
            }
            Log.d("first", "firsthahahahaa")

            updateFoodToDb(realFoodDonationId, binding.editTextUpdateFoodName.text.toString(), binding.editTextUpdateFoodPortion.text.toString(),
                binding.editTextUpdateFoodDate.text.toString(), binding.editTextUpdateFoodAddress.text.toString(),
                binding.editTextUpdateToTime.text.toString(), binding.editTextUpdateFromTime.text.toString())



        }
    }

    private fun updateFoodToDb(foodDonationId:String,foodName: String,foodPortion: String,foodDate: String,
                               foodLocation: String,foodToTime: String,foodFromTime: String,) {

        val currentUser = mAuth.currentUser

        if(imageUri != null) {
            val oldImageRef = mStorageRef.child(foodDonationId)

            oldImageRef.delete().addOnSuccessListener {
                binding.progressBarUpload.visibility = View.VISIBLE
                binding.textViewProgress.visibility = View.VISIBLE

                mStorageRef.child(foodDonationId).putFile(imageUri!!)
                    .addOnProgressListener { taskSnapshot ->
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                        binding.progressBarUpload.progress = progress
                        binding.textViewProgress.text = "$progress %"
                    }
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                            val newImageUrl = url.toString()

                            val updateFoodData = mapOf<String, String>(
                                "foodName" to foodName,
                                "foodPortion" to foodPortion,
                                "foodLocation" to foodLocation,
                                "foodDate" to foodDate,
                                "foodFromTime" to foodFromTime,
                                "foodToTime" to foodToTime,
                                "foodURL" to newImageUrl
                            )

                            mRef.child(foodDonationId).updateChildren(updateFoodData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Data is Updated!", Toast.LENGTH_LONG)
                                        .show()

                                    val intent =
                                        Intent(
                                            this@DonorUpdateFoodActivity,
                                            DonorActivity::class.java
                                        )
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Data fail to Update!", Toast.LENGTH_LONG)
                                        .show()
                                }
                        }

                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Image Upload failed!", Toast.LENGTH_LONG)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("DonorUpdateFoodActivity", "Error deleting old image: ${e.message}")
                        Toast.makeText(this, "Failed to delete old image", Toast.LENGTH_LONG).show()

                    }
            }

        } else {
            // No new image selected, update other data only
            val updateFoodData = mapOf<String, Any>(
                "foodName" to foodName,
                "foodPortion" to foodPortion,
                "foodLocation" to foodLocation,
                "foodDate" to foodDate,
                "foodFromTime" to foodFromTime,
                "foodToTime" to foodToTime
            )

            mRef.child(foodDonationId).updateChildren(updateFoodData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data is Updated!", Toast.LENGTH_LONG).show()

                    val intent =
                        Intent(this@DonorUpdateFoodActivity, DonorActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Data failed to Update!", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}