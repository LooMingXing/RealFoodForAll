package com.example.realfoodforall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.realfoodforall.databinding.ActivityDonorUpdateFoodBinding
import com.example.realfoodforall.databinding.ActivityReceiverUpdateFoodBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class ReceiverUpdateFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverUpdateFoodBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var realFoodDonationId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverUpdateFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase?.reference!!.child("FoodDonation")

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
            .into(binding.imageViewReceiverFood)

        binding.textViewShowFoodName.text = foodName
        binding.textViewShowFoodLocation.text = foodLocation
        binding.textViewShowFoodDate.text = foodDate

        binding.textViewReceiverFoodToTime.text = foodToTime
        binding.textViewReceiverFromTime.text = foodFromTime


        binding.buttonWantFood.setOnClickListener {
            showConfirmationDialog()
        }

    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Food Request")
        builder.setMessage("Do you want to request this food?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Deduct the food portion and update the database
            deductFoodPortion(1) // Deduct 1 portion, you can adjust as needed
        }
        builder.setNegativeButton("No") { _, _ ->
            // Cancel the request
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deductFoodPortion(portionToDeduct: Int) {
        val foodDonationId = realFoodDonationId

        val foodPortionRef = mRef.child(foodDonationId).child("foodPortion")

        foodPortionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentPortion = dataSnapshot.getValue(Int::class.java)

                if (currentPortion != null && currentPortion >= portionToDeduct) {
                    val newPortion = currentPortion - portionToDeduct

                    foodPortionRef.setValue(newPortion)

                    Toast.makeText(this@ReceiverUpdateFoodActivity, "Food requested successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ReceiverUpdateFoodActivity, "Invalid request.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ReceiverUpdateFoodActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}