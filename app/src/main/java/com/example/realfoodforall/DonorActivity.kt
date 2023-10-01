package com.example.realfoodforall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.realfoodforall.databinding.ActivityDonorBinding
import com.example.realfoodforall.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.Locale

class DonorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonorBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mRef: DatabaseReference

    private lateinit var foodList: ArrayList<DonorFoodData>
    private lateinit var adapter: FoodAdapterClass
    var eventListener:ValueEventListener? = null

    private lateinit var searchViewDonor: SearchView
    private lateinit var searchList: ArrayList<DonorFoodData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbarDonorHome)

        val gridLayoutManager = GridLayoutManager(this@DonorActivity, 1)
        binding.recyclerViewFoodDonation.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@DonorActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        foodList = arrayListOf<DonorFoodData>()
        searchList = arrayListOf<DonorFoodData>()
        searchViewDonor = binding.searchViewDonor

        adapter = FoodAdapterClass(this@DonorActivity, searchList)
        binding.recyclerViewFoodDonation.adapter = adapter
        mRef = FirebaseDatabase.getInstance().getReference("FoodDonation")
        dialog.show()

        eventListener = mRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                for(itemSnapshot in snapshot.children){
                    val foodDonationId = itemSnapshot.key
                    val foodClass = itemSnapshot.getValue(DonorFoodData::class.java)

                    foodClass?.foodDonationId = foodDonationId

                    if(foodClass?.foodDonorUID == mAuth.currentUser?.uid){
                        foodList.add(foodClass!!)
                    }

                }
                searchList.addAll(foodList)
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }

        })

        searchViewDonor.clearFocus()
        searchViewDonor.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewDonor.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    foodList.forEach{
                        if(it.foodName?.toLowerCase(Locale.getDefault())!!.contains(searchText)){
                            searchList.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    searchList.clear()
                    searchList.addAll(foodList)
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })

        binding.floatingActionButtonAddFood.setOnClickListener{
            val intent = Intent(baseContext, DonorCreateFoodActivity::class.java)
            startActivity(intent)
        }

        binding.floatingActionButtonGenerateReport.setOnClickListener {
            val intent = Intent(this@DonorActivity, DonorReportActivity::class.java )
            startActivity(intent)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.donor_menu_navigate, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.donor_profile ->{
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.donor_logout -> {
                mAuth.signOut()
                Toast.makeText(baseContext, "Logout Successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }
}