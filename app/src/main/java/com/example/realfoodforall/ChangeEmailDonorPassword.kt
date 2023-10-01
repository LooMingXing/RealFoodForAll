package com.example.realfoodforall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.realfoodforall.databinding.ActivityChangeEmailDonorPasswordBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class ChangeEmailDonorPassword : AppCompatActivity() {

    private lateinit var binding: ActivityChangeEmailDonorPasswordBinding
    private lateinit var mStorageRef: StorageReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailDonorPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser
        var uid = ""
        uid = currentUser?.uid.toString()

        binding.imageViewBackProf.setOnClickListener(){
            intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)

        }


        val userRef = FirebaseDatabase.getInstance().getReference("user")
        var old_email = ""

        userRef.child(uid).child("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = snapshot.value as String?
                    old_email = email.toString()
                    binding.editTextChangeEmail.setText(old_email)

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    val errorMessage = "Database Error: " + error.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })



        binding.buttonConfirmEmailUpd.setOnClickListener {
            val newEmail = binding.editTextUpdateEmailAddress.text.toString()
            val currentPassword = binding.editTextPass.text.toString()

            if (newEmail.isEmpty() || currentPassword.isEmpty())  {
                // Show a warning if email or password fields are empty
                Toast.makeText(applicationContext, "Email and password are required.", Toast.LENGTH_SHORT).show()
            } else {
                val user = mAuth.currentUser

//                user?.reauthenticate(credential)
//                    ?.addOnCompleteListener { reauthResult ->
//                        if (reauthResult.isSuccessful) {
//                            // Update the user's email in Firebase Authentication
//                            user!!.updateEmail(newEmail)
//                                .addOnCompleteListener { updateEmailResult ->
//                                    if (updateEmailResult.isSuccessful) {
//                                        // Email updated successfully
//                                        Toast.makeText(applicationContext, "Email updated", Toast.LENGTH_SHORT).show()
//                                        // Update the email in the Firebase Realtime Database
//                                        updateUserProfileInDatabase(uid, newEmail)
//                                        val intent = Intent(this, ProfileActivity::class.java)
//                                        startActivity(intent)
//                                    } else {
//                                        // Handle email update failure
//                                        Toast.makeText(applicationContext, "Failed to update email", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                        } else {
//                            // Handle reauthentication failure
//                            Toast.makeText(applicationContext, "Reauthentication failed", Toast.LENGTH_SHORT).show()
//                        }
//                    }
                mAuth.signInWithEmailAndPassword(old_email, currentPassword)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            mAuth.currentUser!!.updateEmail(newEmail)
                                .addOnCompleteListener{ task->
                                    Log.d("test", "haha")
                                    if(task.isSuccessful){
                                        Toast.makeText(applicationContext, "Email updated", Toast.LENGTH_SHORT).show()
                                        updateUserProfileInDatabase(uid, newEmail)
                                        val intent = Intent(this, ProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{
                                        Toast.makeText(applicationContext, "Failed to update email", Toast.LENGTH_SHORT).show()
                                    }
                                }
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
        val email = binding.editTextChangeEmail.text.toString()
        if(binding.editTextChangeEmail.toString() == ""){
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return false
        }
        return true




    }



}