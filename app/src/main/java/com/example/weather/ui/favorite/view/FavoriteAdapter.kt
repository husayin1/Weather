package com.example.weather.ui.favorite.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.databinding.FavCardBinding
import com.example.weather.model.response.SavedLocations
import com.example.weather.utilites.Converters
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FavoriteAdapter (
    val myListener:(SavedLocations)->Unit
): ListAdapter<SavedLocations, FavoriteAdapter.FavoriteViewHolder>(FavDiffUtil()) {
    inner class FavoriteViewHolder(var binding:FavCardBinding):RecyclerView.ViewHolder(binding.root)


    lateinit var binding:FavCardBinding
    private val TAG ="FavoriteAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater:LayoutInflater=parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)as LayoutInflater
        binding= FavCardBinding.inflate(inflater,parent,false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.binding.textViewCountry.text=currentItem.address
        holder.binding.constraintLayout.setOnClickListener {
            Log.i(TAG, "onBindViewHolder:Current Item Detials ")
            Log.i(TAG, "onBindViewHolder: Swap to Delete")
            myListener.invoke(currentItem)
        }
        holder.binding.textViewTemp.text=Converters.convertTemperature(currentItem.currentTemp?:0.0,holder.binding.root.context)
        holder.binding.textViewDescription.text=currentItem.currentDescription
//        formatDate(currentItem.lastCheckedTime,holder.binding.textViewDate)
        Log.i(TAG, "onBindViewHolder: ${currentItem.lastCheckedTime} ")
        holder.binding.textViewDate.text=formatDate(currentItem.lastCheckedTime)
        Glide.with(holder.binding.root)
            .load(Converters.getWeatherImage(currentItem.icon))
            .into(holder.binding.imgWeather)
    }

    private fun formatDate(dateTime: Long):String {
        val formatDate = SimpleDateFormat("MMM dd, yyyy - EEE hh:mm a", Locale.getDefault())
        formatDate.timeZone = TimeZone.getTimeZone("GMT+2")
        return formatDate.format(dateTime)
    }

}
class FavDiffUtil:DiffUtil.ItemCallback<SavedLocations>(){
    override fun areItemsTheSame(oldItem: SavedLocations, newItem: SavedLocations): Boolean {
        return oldItem===newItem
    }

    override fun areContentsTheSame(oldItem: SavedLocations, newItem: SavedLocations): Boolean {
        return oldItem==newItem
    }

}