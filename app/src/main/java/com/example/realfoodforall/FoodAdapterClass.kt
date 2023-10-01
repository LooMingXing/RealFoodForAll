package com.example.realfoodforall

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class FoodAdapterClass(private val context: Context, private val foodList:ArrayList<DonorFoodData>):RecyclerView.Adapter<FoodAdapterClass.ViewHolderClass>() {

    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvFoodName:TextView = itemView.findViewById(R.id.foodNameItem)
        val rvFoodPortion:TextView = itemView.findViewById(R.id.foodPortionItem)
        val rvFoodImage:ImageView = itemView.findViewById(R.id.foodImageItem)
        val foodCard:CardView = itemView.findViewById(R.id.foodCard)
        val rvEditButton:Button = itemView.findViewById(R.id.buttonEditFoodDonation)
        val rvDeleteButton:Button = itemView.findViewById(R.id.buttonDeleteFoodDonation)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.food_record_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = foodList[position]
        holder.rvFoodName.text = currentItem.foodName
        holder.rvFoodPortion.text = currentItem.foodPortion
        Glide.with(context).load(currentItem.foodURL).into(holder.rvFoodImage)

        holder.rvEditButton.setOnClickListener{
            val intent = Intent(context, DonorUpdateFoodActivity::class.java)
            intent.putExtra("Food Donation Id", foodList[holder.adapterPosition].foodDonationId)
            intent.putExtra("Image", foodList[holder.adapterPosition].foodURL)
            intent.putExtra("Food Name", foodList[holder.adapterPosition].foodName)
            intent.putExtra("Food Portion", foodList[holder.adapterPosition].foodPortion)
            intent.putExtra("Food Location", foodList[holder.adapterPosition].foodLocation)
            intent.putExtra("Food Date", foodList[holder.adapterPosition].foodDate)
            intent.putExtra("Food ToTime", foodList[holder.adapterPosition].foodToTime)
            intent.putExtra("Food FromTime", foodList[holder.adapterPosition].foodFromTime)

            context.startActivity(intent)
        }

        holder.rvDeleteButton.setOnClickListener {
            showDeleteConfirmationDialog(position)
            true
        }

    }

    private fun showDeleteConfirmationDialog(position: Int) {

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Delete Food")
            .setMessage("Are you sure you want to delete this food item?")
            .setPositiveButton("Delete") { _, _ ->

                val firebaseRef = FirebaseDatabase.getInstance().getReference("FoodDonation")
                val storageRef = FirebaseStorage.getInstance().getReference("Images")
                val foodDonationId = foodList[position].foodDonationId.toString()

                storageRef.child(foodDonationId).delete()

                firebaseRef.child(foodDonationId).removeValue()
                    .addOnSuccessListener {
                        foodList.removeAt(position)
                        notifyItemRemoved(position)

                        // Provide feedback to the user
                        Toast.makeText(context, "Food item deleted", Toast.LENGTH_LONG).show()

                    }
                    .addOnFailureListener {error ->
                        Toast.makeText(context,"error ${error.message}" ,Toast.LENGTH_SHORT).show()
                    }


            }
            .setNegativeButton("Cancel") { dialog, _ ->
                Toast.makeText(context,"Cancelled" ,Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}