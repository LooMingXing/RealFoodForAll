package com.example.realfoodforall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ReceiverHistoryActivity : AppCompatActivity() {

    private lateinit var historyAdapter: HistoryAdapterClass
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyList: ArrayList<HistoryFoodData>
    private lateinit var historyRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receiver_history)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        historyList = ArrayList()
        historyAdapter = HistoryAdapterClass(this, historyList)
        historyRecyclerView = findViewById(R.id.recyclerViewReceiverHistory)
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Initialize Firebase database reference
        historyRef = FirebaseDatabase.getInstance().getReference("History")

        // Create a query to filter data based on the userid
        val query = historyRef.orderByChild("userid").equalTo(userId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                historyList.clear()
                for (itemSnapshot in snapshot.children) {
                    // Access data from itemSnapshot directly
                    val foodName = itemSnapshot.child("FoodName").getValue(String::class.java)
                    val foodLocation = itemSnapshot.child("FoodLocation").getValue(String::class.java)
                    val foodDate = itemSnapshot.child("FoodDate").getValue(String::class.java)
                    val foodToTime = itemSnapshot.child("FoodToTime").getValue(String::class.java)
                    val foodFromTime = itemSnapshot.child("FoodFromTime").getValue(String::class.java)
                    val foodUrl = itemSnapshot.child("foodurl").getValue(String::class.java)

                    // Create a custom object to hold the data
                    val foodData = HistoryFoodData(
                        foodName,
                        foodLocation,
                        foodDate,
                        foodToTime,
                        foodFromTime,
                        foodUrl
                    )

                    historyList.add(foodData)
                }
                historyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if data retrieval fails
            }
        })

    }
}
