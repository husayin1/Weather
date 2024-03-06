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
import com.example.weather.databinding.ViewholderDayBinding
import com.example.weather.model.response.DailyItem
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.Converters
import com.example.weather.utilites.Helper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DailyAdapter(private val tempUnit:String) :
    ListAdapter<DailyItem, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    private lateinit var binding: ViewholderDayBinding
    inner class DailyViewHolder(val binding: ViewholderDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewholderDayBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val currentDay = getItem(position)
        holder.binding.textViewDay.text =Converters.convertDate(currentDay.dt?.toLong())
        Glide.with(holder.binding.root)
            .load(Helper.getWeatherImage(currentDay.weather?.get(0)?.icon))
            .into(holder.binding.imageView)

        holder.binding.textViewStatus.text = currentDay.weather?.get(0)?.main
        val defaultMinTemp=currentDay.temp?.min
        val defaultMaxTemp=currentDay.temp?.max
        holder.binding.textViewLow.text = when(tempUnit){
            CONSTANTS.celsius->{
                Converters.convertTemperature(defaultMinTemp!!,holder.binding.root.context)
            }
            CONSTANTS.fahrenheit->{
                Converters.convertTemperature(defaultMinTemp!!,holder.binding.root.context)
            }
            else->{
                Converters.convertTemperature(defaultMinTemp!!,holder.binding.root.context)
            }
        }
        holder.binding.textViewHigh.text = when(tempUnit){
            CONSTANTS.celsius->{
                Converters.convertTemperature(defaultMaxTemp!!,holder.binding.root.context)
            }
            CONSTANTS.fahrenheit->{
                Converters.convertTemperature(defaultMaxTemp!!,holder.binding.root.context)
            }
            else->{
                Converters.convertTemperature(defaultMaxTemp!!,holder.binding.root.context)
            }
        }
    }
}

class DailyDiffUtil : DiffUtil.ItemCallback<DailyItem>() {
    override fun areItemsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: DailyItem, newItem: DailyItem): Boolean {
        return oldItem == newItem
    }
}