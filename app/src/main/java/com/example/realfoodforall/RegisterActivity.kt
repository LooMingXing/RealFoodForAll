package com.example.realfoodforall

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.realfoodforall.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var mStorageRef: StorageReference
    private lateinit var imageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase?.reference!!.child("user")

        //        For Firebase storage image
        mStorageRef = FirebaseStorage.getInstance().getReference("profile_pic")

        //        For fetching the image uri!
        val pickImageFromGalleryForResult =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.imageViewProfilePic.setImageURI(it)

                if (it != null) {
                    imageUri = it
                }
            }

        // for open gallery to choose image
        binding.buttonProfilePic.setOnClickListener {
            pickImageFromGalleryForResult.launch("image/*")
        }

        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val email = binding.editTextEmailRegister.text.toString().trim()
            val password = binding.editTextPasswordRegister.text.toString()
            val confirmPassword = binding.editTextPasswordConfirm.text.toString()
            var role = ""

            if(username.isEmpty()){
                binding.editTextUsername.error = "Username is required!"
            }

            if(email.isEmpty())
                binding.editTextEmailRegister.error = "Email is Required!"

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                binding.editTextEmailRegister.error = "Please Provide A Valid Email!"

            if(password.isEmpty())
                binding.editTextPasswordRegister.error = "Password is Required!"

            if (confirmPassword != password)
                binding.editTextPasswordConfirm.error = "Password Mismatch"

            if(binding.radioGroup.checkedRadioButtonId == -1)
                binding.radioButtonReceiver.error = "Must choose one!"

            role = if(binding.radioGroup.checkedRadioButtonId == binding.radioButtonDonor.id)
                "Donor"
            else
                "Receiver"

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password) {
                registerUser(binding.editTextUsername.text.toString(),binding.editTextEmailRegister.text.toString(),
                    binding.editTextPasswordRegister.text.toString(), role)
            }

            binding.textViewSignIn.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.textViewSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun uploadImageToStorage(uid: String?) {
        val storageRef =
            mStorageRef.child("$uid/profilePic") // Create a reference with the UID as a subfolder

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Handle a successful upload
                // Get the download URL of the uploaded image
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Once you have the download URL, save it to the Realtime Database
                    saveImageUriToDatabase(uid, downloadUrl.toString())
                }
            }
    }

    private fun saveImageUriToDatabase(uid: String?, uri: String) {
        val addToDatabase = mRef.child(uid!!)
        addToDatabase.child("profilePic").setValue(uri) // Save the URI as a string in the database
    }

    private fun registerUser(username:String, email:String, password:String, role:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("", "createUserWithEmail:success")

                    val currentUser = mAuth.currentUser
                    val addToDatabase = mRef.child(currentUser?.uid!!)
                    addToDatabase.child("username").setValue(username)
                    addToDatabase.child("email").setValue(email)
                    addToDatabase.child("role").setValue(role)

                    uploadImageToStorage(currentUser?.uid)

                    Toast.makeText(baseContext,"User has been registered successfully!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            baseContext,
                            "Email is already registered. Please use a different email.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.w("", "createUserWithEmail:Failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Registration failed! Please try again later.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }


}