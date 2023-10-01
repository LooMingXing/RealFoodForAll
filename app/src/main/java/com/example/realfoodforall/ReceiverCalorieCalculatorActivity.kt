package com.example.realfoodforall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class ReceiverCalorieCalculatorActivity : AppCompatActivity() {

    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioButtonMale: RadioButton
    private lateinit var radioButtonFemale: RadioButton
    private lateinit var editTextAge: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var buttonReset: Button
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver_calorie_calculator)

        radioGroupGender = findViewById(R.id.radioGroupGender)
        radioButtonMale = findViewById(R.id.radioButtonMale)
        radioButtonFemale = findViewById(R.id.radioButtonFemale)
        editTextAge = findViewById(R.id.editTextAge)
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextHeight = findViewById(R.id.editTextHeight)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        buttonReset = findViewById(R.id.buttonReset)
        textViewResult = findViewById(R.id.textViewResult)

        buttonCalculate.setOnClickListener { calculateCalories() }
        buttonReset.setOnClickListener { resetInputs() }
    }

    private fun calculateCalories() {
        val age = editTextAge.text.toString().toIntOrNull() ?: 0
        val weight = editTextWeight.text.toString().toFloatOrNull() ?: 0f
        val height = editTextHeight.text.toString().toFloatOrNull() ?: 0f

        val bmr: Double
        val genderId = radioGroupGender.checkedRadioButtonId

        if (genderId == R.id.radioButtonMale) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else if (genderId == R.id.radioButtonFemale) {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        } else {
            textViewResult.text = "Please select your gender"
            return
        }

        // Calculate daily calorie needs using the Harris-Benedict equation
        val dailyCalories = when {
            age <= 0 || weight <= 0 || height <= 0 -> 0
            genderId == -1 -> 0
            genderId == R.id.radioButtonMale -> (bmr * 1.55).toInt() // Moderately active
            genderId == R.id.radioButtonFemale -> (bmr * 1.55).toInt() // Moderately active
            else -> 0
        }

        textViewResult.text = "$dailyCalories"
    }

    private fun resetInputs() {
        editTextAge.text.clear()
        editTextWeight.text.clear()
        editTextHeight.text.clear()
        radioGroupGender.clearCheck()
        textViewResult.text = ""
    }
}