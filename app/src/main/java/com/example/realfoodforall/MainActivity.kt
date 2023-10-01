package com.example.realfoodforall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.realfoodforall.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mRef = mDatabase.reference.child("user")

        binding.buttonLogin.setOnClickListener{
            val email = binding.editTextEmailLogin.text.toString().trim()
            val password = binding.editTextPasswordLogin.text.toString()

            if(email.isEmpty())
                binding.editTextEmailLogin.error = "Email is Required!"

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                binding.editTextEmailLogin.error = "Please Provide A Valid Email!"

            if(password.isEmpty())
                binding.editTextPasswordLogin.error = "Password is Required!"

            if(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.isNotEmpty()) {
                loginUser(binding.editTextEmailLogin.text.toString(), binding.editTextPasswordLogin.text.toString())
            }
        }

        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.textViewCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d( "" , "signInWithEmail:success")

                    val currentUser = mAuth.currentUser
                    mRef.child(currentUser?.uid!!).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val username = snapshot.child("username").getValue(String::class.java)
                            val role = snapshot.child("role").getValue(String::class.java)

                            if (role == "Donor") {
                                Toast.makeText(baseContext, "Welcome Donor $username", Toast.LENGTH_LONG).show()
                                val intent = Intent(baseContext, DonorActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (role == "Receiver") {
                                Toast.makeText(baseContext, "Welcome Receiver $username", Toast.LENGTH_LONG).show()
                                val intent = Intent(baseContext, ReceiverActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                } else {
                    Log.w("", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Login Failed! Please try again later", Toast.LENGTH_LONG).show()
                }


            }
    }
}