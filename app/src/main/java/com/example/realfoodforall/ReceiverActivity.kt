package com.example.realfoodforall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.realfoodforall.databinding.ActivityDonorBinding
import com.example.realfoodforall.databinding.ActivityDonorUpdateFoodBinding
import com.example.realfoodforall.databinding.ActivityReceiverBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var receiverFoodList: ArrayList<DonorFoodData>
    private lateinit var receiverAdapter: ReceiverFoodAdapterClass
    var eventListener:ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@ReceiverActivity, 1)
        binding.recyclerViewReceiver.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@ReceiverActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        receiverFoodList = ArrayList()
        receiverAdapter = ReceiverFoodAdapterClass(this@ReceiverActivity, receiverFoodList)
        binding.recyclerViewReceiver.adapter = receiverAdapter
        mRef = FirebaseDatabase.getInstance().getReference("FoodDonation")
        dialog.show()

        eventListener = mRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                receiverFoodList.clear()
                for(itemSnapshot in snapshot.children){
                    val foodDonationId = itemSnapshot.key
                    val foodClass = itemSnapshot.getValue(DonorFoodData::class.java)

                    foodClass?.foodDonationId = foodDonationId
                    receiverFoodList.add(foodClass!!)


                }
                receiverAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

        binding.floatingActionButtonCalorieCalculator.setOnClickListener {
            val intent = Intent(this@ReceiverActivity, ReceiverCalorieCalculatorActivity::class.java)
            startActivity(intent)
        }


    }
}