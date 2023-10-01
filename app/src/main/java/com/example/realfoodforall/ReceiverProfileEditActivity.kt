package com.example.realfoodforall

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.example.realfoodforall.databinding.ActivityReceiverProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ReceiverProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverProfileEditBinding
    private lateinit var mStorageRef: StorageReference

    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var uid = ""
        var reset_img=""
        var reset_name = ""
        var reset_email=""
        uid = currentUser?.uid.toString()

        val pickImageFromGalleryForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imageViewProfPicEditReceiver.setImageURI(it)

                if (it != null) {
                    imageUri = it
                }
            }

        binding.buttonChangeImageReceiver.setOnClickListener {
            pickImageFromGalleryForResult.launch("image/*")
        }

        binding.imageView2Receiver.setOnClickListener(){
            val intent = Intent(this, ReceiverProfileActivity::class.java)
            startActivity(intent)

        }

        val userRef = FirebaseDatabase.getInstance().getReference("user")
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pic")

        userRef.child(uid).child("profilePic")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val image = snapshot.value as String?
                    reset_img = image.toString()
                    if (image != null) {
                        Glide.with(applicationContext)
                            .load(image)
                            .into(binding.imageViewProfPicEditReceiver)

                    } else {
                        // Handle the case where the username is not found
                        val errorMessage = "Username not found."
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    val errorMessage = "Database Error: " + error.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })


        userRef.child(uid).child("username")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.value as String?
                    reset_name = username.toString()
                    if (username != null) {
                        binding.editTextProfileNameReceiver.setText(username)
                    } else {
                        // Handle the case where the username is not found
                        val errorMessage = "Username not found."
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    val errorMessage = "Database Error: " + error.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })

        userRef.child(uid).child("email")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val email = snapshot.value as String?
                    reset_email = email.toString()
                    if (email != null) {
                        binding.editTextProfEmailReceiver.setText(email)
                    } else {
                        // Handle the case where the username is not found
                        val errorMessage = "Email not found."
                        Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                    val errorMessage = "Database Error: " + error.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })


        binding.buttonResetEditReceiver.setOnClickListener(){

            Glide.with(applicationContext)
                .load(reset_img)
                .into(binding.imageViewProfPicEditReceiver)

            binding.editTextProfileNameReceiver.setText(reset_name)
            binding.editTextProfEmailReceiver.setText(reset_email)


        }

        binding.buttonChangeEmailReceiver.setOnClickListener(){
            val intent = Intent(this, ReceiverProfileEmailChange::class.java)
            startActivity(intent)
        }


        binding.buttonConfirmEditReceiver.setOnClickListener {
            // Get the updated information from the EditText and the imageUri
            val newUsername = binding.editTextProfileNameReceiver.text.toString()
            val newEmail = binding.editTextProfEmailReceiver.text.toString()

            // Update the user's profile information in the database
            updateUserProfileInDatabase(uid, newUsername, newEmail)
            val intent = Intent(this, ReceiverProfileActivity::class.java)
            startActivity(intent)
        }


    }

    private fun updateUserProfileInDatabase(uid: String, newUsername: String, newEmail: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("user")

        // Update the username and email in the database
        userRef.child(uid).child("username").setValue(newUsername)
        userRef.child(uid).child("email").setValue(newEmail)

        // Update the user's display name in Firebase Authentication
        val mAuth = FirebaseAuth.getInstance()


        if (imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("profile_pic").child(uid)
            storageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    userRef.child(uid).child("profilePic").setValue(uri.toString())
                }.addOnFailureListener { exception ->
                    // Handle the error
                    val errorMessage = "Image Upload Error: " + exception.message
                    Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                // Handle the error
                val errorMessage = "Image Upload Error: " + exception.message
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Inform the user that the profile has been updated
        Toast.makeText(applicationContext, "Profile Updated", Toast.LENGTH_SHORT).show()
    }





}