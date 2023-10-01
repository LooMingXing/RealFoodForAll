package com.example.realfoodforall

import android.R.attr.bitmap
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.example.realfoodforall.databinding.ActivityReceiverProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class ReceiverProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverProfileBinding
    private lateinit var mStorageRef: StorageReference

    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        var uid = ""
        uid = currentUser?.uid.toString()





        val userRef = FirebaseDatabase.getInstance().getReference("user")
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pic")

        userRef.child(uid).child("profilePic")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val image = snapshot.value as String?
                    if (image != null) {
                        Glide.with(applicationContext)
                            .load(image)
                            .into(binding.imageViewProfilePictReceiver)

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
                    if (username != null) {
                        binding.textViewProfileNameReceiver.text = username
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
                    if (email != null) {
                        binding.textViewUserEmailReceiver.text = email
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


        binding.imageViewBackReceiver.setOnClickListener {
            val intent = Intent(this, ReceiverActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogoutReceiver.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonEditAccReceiver.setOnClickListener{
            val intent = Intent(this, ReceiverProfileEditActivity::class.java)
            startActivity(intent)
        }

    }
}