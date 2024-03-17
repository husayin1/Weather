package com.example.weather.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ViewholderHourlyBinding
import com.example.weather.model.response.HourlyItem
import com.example.weather.utilites.Converters

class HourlyAdapter(private val tempUnit: String) :
    ListAdapter<HourlyItem, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {
    private lateinit var binding: ViewholderHourlyBinding

    inner class HourlyViewHolder(var binding: ViewholderHourlyBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewholderHourlyBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentHour = getItem(position)
        if (currentHour == getItem(0)) {
            holder.binding.textViewHour.text =holder.itemView.context.getText(R.string.now)
        } else if (position >= 1) {
            holder.binding.textViewHour.text = Converters.convertHour(currentHour.dt?.toLong())
        }
        Glide.with(holder.binding.root)
            .load(Converters.getWeatherImage(currentHour.weather?.get(0)?.icon))
            .into(holder.binding.imageViewWeather)

        val defaultTemp = currentHour.temp
        holder.binding.textViewTemp.text = when (tempUnit) {
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