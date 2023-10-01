package com.example.realfoodforall

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReceiverFoodAdapterClass(private val context: Context, private val receiverFoodList:ArrayList<DonorFoodData>):RecyclerView.Adapter<ReceiverFoodAdapterClass.ViewHolderClass>() {

    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvReceiverFoodName: TextView = itemView.findViewById(R.id.foodNameItem)
        val rvReceiverFoodLocation: TextView = itemView.findViewById(R.id.foodLocationItem)
        val rvReceiverFoodImage: ImageView = itemView.findViewById(R.id.foodImageItem)
        val receiverFoodCard: CardView = itemView.findViewById(R.id.receiverFoodCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.receiver_food_record_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return receiverFoodList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = receiverFoodList[position]
        holder.rvReceiverFoodName.text = currentItem.foodName
        holder.rvReceiverFoodLocation.text = currentItem.foodLocation
        Glide.with(context).load(currentItem.foodURL).into(holder.rvReceiverFoodImage)

        holder.receiverFoodCard.setOnClickListener{
            val intent = Intent(context, ReceiverUpdateFoodActivity::class.java)
            intent.putExtra("Food Donation Id", receiverFoodList[holder.adapterPosition].foodDonationId)
            intent.putExtra("Image", receiverFoodList[holder.adapterPosition].foodURL)
            intent.putExtra("Food Name", receiverFoodList[holder.adapterPosition].foodName)
            intent.putExtra("Food Portion", receiverFoodList[holder.adapterPosition].foodPortion)
            intent.putExtra("Food Location", receiverFoodList[holder.adapterPosition].foodLocation)
            intent.putExtra("Food Date", receiverFoodList[holder.adapterPosition].foodDate)
            intent.putExtra("Food ToTime", receiverFoodList[holder.adapterPosition].foodToTime)
            intent.putExtra("Food FromTime", receiverFoodList[holder.adapterPosition].foodFromTime)

            context.startActivity(intent)
        }
    }
}