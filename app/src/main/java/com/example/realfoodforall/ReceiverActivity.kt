package com.example.realfoodforall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
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
import java.util.Locale
import androidx.appcompat.widget.SearchView as AppCompatSearchView

class ReceiverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiverBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference
    private lateinit var receiverFoodList: ArrayList<DonorFoodData>
    private lateinit var receiverAdapter: ReceiverFoodAdapterClass
    var eventListener:ValueEventListener? = null

    private lateinit var searchViewReceiver: android.widget.SearchView
    private lateinit var searchListReceiver: ArrayList<DonorFoodData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@ReceiverActivity, 1)
        binding.recyclerViewReceiver.layoutManager = gridLayoutManager

        setSupportActionBar(binding.toolbarReceiverHome)
        binding.toolbarReceiverHome.setTitle("Receiver Home")

        val builder = AlertDialog.Builder(this@ReceiverActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        receiverFoodList = arrayListOf<DonorFoodData>()
        searchListReceiver = arrayListOf<DonorFoodData>()
        searchViewReceiver = binding.searchViewReceiver

        receiverAdapter = ReceiverFoodAdapterClass(this@ReceiverActivity, searchListReceiver)
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
                    // Check if the portion is greater than 1
                    val foodPortion = foodClass?.foodPortion
                    if (foodPortion?.toIntOrNull() ?: 0 > 0) {
                        receiverFoodList.add(foodClass!!)
                    }

                }
                searchListReceiver.addAll(receiverFoodList)
                receiverAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

        searchViewReceiver.clearFocus()
        searchViewReceiver.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewReceiver.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchListReceiver.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    receiverFoodList.forEach{
                        if(it.foodName?.toLowerCase(Locale.getDefault())!!.contains(searchText)){
                            searchListReceiver.add(it)
                        }
                    }

                }else{
                    searchListReceiver.clear()
                    searchListReceiver.addAll(receiverFoodList)

                }
                return false
            }
        })

        binding.floatingActionButtonCalorieCalculator.setOnClickListener {
            val intent = Intent(this@ReceiverActivity, ReceiverCalorieCalculatorActivity::class.java)
            startActivity(intent)
        }
        binding.floatingActionhistory.setOnClickListener {
            val intent = Intent(this@ReceiverActivity, ReceiverHistoryActivity::class.java)
            startActivity(intent)
        }


    }



    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.receiver_menu_navigate, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.receiver_profile ->{
                val intent = Intent(this, ReceiverProfileActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.receiver_logout -> {
                mAuth.signOut()
                Toast.makeText(baseContext, "Logout Successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }


}