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

class HistoryAdapterClass(private val context: Context, private val historyList: ArrayList<HistoryFoodData>) :
    RecyclerView.Adapter<HistoryAdapterClass.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyFoodName: TextView = itemView.findViewById(R.id.foodNameItem)
        val historyFoodLocation: TextView = itemView.findViewById(R.id.foodLocationItem)
        val historyFoodImage: ImageView = itemView.findViewById(R.id.foodImageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.receiver_history_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = historyList[position]
        holder.historyFoodName.text = currentItem.foodName
        holder.historyFoodLocation.text = currentItem.foodLocation
        Glide.with(context).load(currentItem.foodImageUrl).into(holder.historyFoodImage)

    }
}
