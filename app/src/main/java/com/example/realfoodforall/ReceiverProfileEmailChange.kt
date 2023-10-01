package com.example.realfoodforall

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.example.realfoodforall.databinding.ActivityReceiverProfileEmailChangeBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ReceiverProfileEmailChange : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverProfileEmailChangeBinding
    private lateinit var mStorageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverProfileEmailChangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var uid = ""
        uid = currentUser?.uid.toString()





        binding.imageViewBackProfReceiver.setOnClickListener(){
            intent = Intent(this, ReceiverProfileEditActivity::class.java)
            startActivity(intent)

        }


        val userRef = FirebaseDatabase.getInstance().getReference("user")
        var old_email = ""

        userRef.child(uid).child("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = snapshot.value as String?
                    old_email = email.toString()
                    binding.editTextChangeEmailReceiver.setText(old_email)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    val errorMessage = "Database Error: " + error.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })



        binding.buttonConfirmEmailUpdReceiver.setOnClickListener {
            val newEmail = binding.editTextChangeEmailReceiver.text.toString()
            val currentPassword = binding.editTextPassReceiver.text.toString()

            if (newEmail.isEmpty() || currentPassword.isEmpty()) {
                // Show a warning if email or password fields are empty
                Toast.makeText(applicationContext, "Email and password are required.", Toast.LENGTH_SHORT).show()
            } else {
                val user = mAuth.currentUser
                val credential = EmailAuthProvider
                    .getCredential(old_email, currentPassword)

                user?.reauthenticate(credential)
                    ?.addOnCompleteListener { reauthResult ->
                        if (reauthResult.isSuccessful) {
                            // Update the user's email in Firebase Authentication
                            user!!.updateEmail(newEmail)
                                .addOnCompleteListener { updateEmailResult ->
                                    if (updateEmailResult.isSuccessful) {
                                        // Email updated successfully
                                        Toast.makeText(applicationContext, "Email updated", Toast.LENGTH_SHORT).show()
                                        // Update the email in the Firebase Realtime Database
                                        updateUserProfileInDatabase(uid, newEmail)
                                        val intent = Intent(this, ReceiverProfileActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // Handle email update failure
                                        Toast.makeText(applicationContext, "Failed to update email", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // Handle reauthentication failure
                            Toast.makeText(applicationContext, "Reauthentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            }
        }
    }
    private fun updateUserProfileInDatabase(uid: String, newEmail: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("user")

        // Update the username and email in the database
        userRef.child(uid).child("email").setValue(newEmail)

        // Update the user's display name in Firebase Authentication
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        // Inform the user that the profile has been updated
        Toast.makeText(applicationContext, "Profile Updated", Toast.LENGTH_SHORT).show()


    }

    private fun checkEmailField(): Boolean {
        val email = binding.editTextChangeEmailReceiver.text.toString()
        if(binding.editTextChangeEmailReceiver.toString() == ""){
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true




    }



}


