package com.example.weather.features.home

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.databinding.ViewholderHourlyBinding
import com.example.weather.model.response.HourlyItem
import com.example.weather.utilites.Converters
import com.example.weather.utilites.Helper

class HourlyAdapter(private val tempUnit: String) :
    ListAdapter<HourlyItem, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {
    private lateinit var binding: ViewholderHourlyBinding

    class HourlyViewHolder(var binding: ViewholderHourlyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewholderHourlyBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentHour = getItem(position)
        holder.binding.textViewHour.text = Converters.convertHour(currentHour.dt?.toLong())
        Glide.with(holder.binding.root)
            .load(Helper.getWeatherImage(currentHour.weather?.get(0)?.icon))
            .into(holder.binding.imageViewWeather)
        val defaultTemp = currentHour.temp
        holder.binding.textViewTemp.text= when(tempUnit){
                "celsius" -> {
                    Converters.convertTemperature(defaultTemp!!, holder.binding.root.context)
                }
                "fahrenheit" -> {
                    Converters.convertTemperature(defaultTemp!!, holder.binding.root.context)
                }
                else -> {
                    Converters.convertTemperature(defaultTemp!!, holder.binding.root.context)
                }
            }
        }

}

class HourlyDiffUtil : DiffUtil.ItemCallback<HourlyItem>() {
    override fun areItemsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: HourlyItem, newItem: HourlyItem): Boolean {
        return oldItem == newItem
    }
}