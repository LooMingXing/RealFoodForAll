package com.example.realfoodforall

import android.content.Intent
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

            // Get all the food details from the intent
            val foodName = binding.textViewShowFoodName.text.toString()
            val foodLocation = binding.textViewShowFoodLocation.text.toString()
            val foodDate = binding.textViewShowFoodDate.text.toString()
            val foodToTime = binding.textViewReceiverFoodToTime.text.toString()
            val foodFromTime = binding.textViewReceiverFromTime.text.toString()
            val foodImageUrl = intent.getStringExtra("Image") ?: ""

            // Call updateHistory with all the food details and imageUrl
            updateHistory(foodName, foodLocation, foodDate, foodToTime, foodFromTime, foodImageUrl)
            navigateToHistoryPage()
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
                val currentPortionStr = dataSnapshot.getValue(String::class.java)

                try {
                    // Attempt to convert the currentPortionStr to an integer
                    val currentPortion = currentPortionStr?.toInt()

                    if (currentPortion != null && currentPortion >= portionToDeduct) {
                        val newPortion = (currentPortion - portionToDeduct).toString()

                        foodPortionRef.setValue(newPortion)

                        Toast.makeText(this@ReceiverUpdateFoodActivity, "Food requested successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ReceiverUpdateFoodActivity, "Invalid request.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    // Handle the case where the "foodPortion" field is not a valid integer
                    Toast.makeText(this@ReceiverUpdateFoodActivity, "Invalid data format.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ReceiverUpdateFoodActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateHistory(foodName: String, foodLocation: String, foodDate: String, foodToTime: String, foodFromTime: String, imageUrl: String) {
        val user = mAuth.currentUser
        val userId = user?.uid

        if (userId != null) {
            // Reference to the "History" node in the database
            val historyRef = FirebaseDatabase.getInstance().getReference("History")

            // Create a new entry for the history
            val historyEntryRef = historyRef.push()
            val historyEntryId = historyEntryRef.key

            if (historyEntryId != null) {
                // Prepare the data for the history entry
                val historyData = HashMap<String, Any>()
                historyData["FoodName"] = foodName
                historyData["FoodLocation"] = foodLocation
                historyData["FoodDate"] = foodDate
                historyData["FoodToTime"] = foodToTime
                historyData["FoodFromTime"] = foodFromTime
                historyData["foodurl"] = imageUrl
                historyData["userid"] = userId
                // Add more fields as needed

                // Set the data for the history entry
                historyEntryRef.setValue(historyData)
            }
        }
    }



    private fun navigateToHistoryPage() {
        val intent = Intent(this, ReceiverHistoryActivity::class.java)
        startActivity(intent)
        finish() // Optionally, you can finish the current activity if you don't want to return to it
    }

}