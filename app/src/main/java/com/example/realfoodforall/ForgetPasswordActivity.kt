package com.example.realfoodforall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.realfoodforall.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.buttonSubmitForgetPassword.setOnClickListener {
            val email = binding.editTextEmailForgetPassword.text.toString()

            if(email.isEmpty()){
                binding.editTextEmailForgetPassword.error = "Please enter your email!"
                return@setOnClickListener
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Email sent successfully to reset your password", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}