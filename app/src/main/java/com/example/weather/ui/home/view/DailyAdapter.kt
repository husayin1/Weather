package com.example.weather.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.databinding.ViewholderDayBinding
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.SavedLocations
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.Converters

class DailyAdapter(
    private val tempUnit:String,
    val myListener:(DailyItem)->Unit
) :
    ListAdapter<DailyItem, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    private lateinit var binding: ViewholderDayBinding
    private val TAG = "DailyAdapter"
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
        if(position==0){
            holder.binding.textViewDay.text =holder.itemView.context.getText(R.string.today)
        }else if(position>0){
            holder.binding.textViewDay.text =Converters.convertDate(currentDay.dt?.toLong())
        }
        Glide.with(holder.binding.root)
            .load(Converters.getWeatherImage(currentDay.weather?.get(0)?.icon))
            .into(holder.binding.imageView)
        binding.constraintClick.setOnClickListener {
            myListener.invoke(currentDay)
        }


        holder.binding.textViewStatus.text = currentDay.weather?.get(0)?.description?.capitalize()
        val defaultMinTemp=currentDay.temp?.min
        val defaultMaxTemp=currentDay.temp?.max
        holder.binding.textViewLow.text = when(tempUnit){
            CONSTANTS.celsius->{
                Converters.convertTemperature(defaultMinTemp?:0.0,holder.binding.root.context)
            }
            CONSTANTS.fahrenheit->{
                Converters.convertTemperature(defaultMinTemp?:0.0,holder.binding.root.context)
            }CONSTANTS.kelvin->{
                Converters.convertTemperature(defaultMaxTemp?:0.0,holder.binding.root.context)
            }else->{
                Converters.convertTemperature(defaultMinTemp?:0.0,holder.binding.root.context)
            }
        }
        holder.binding.textViewHigh.text = when(tempUnit){
            CONSTANTS.celsius->{
                Converters.convertTemperature(defaultMaxTemp?:0.0,holder.binding.root.context)
            }
            CONSTANTS.fahrenheit->{
                Converters.convertTemperature(defaultMaxTemp?:0.0,holder.binding.root.context)
            }
            CONSTANTS.kelvin->{
                Converters.convertTemperature(defaultMaxTemp?:0.0,holder.binding.root.context)
            }else->{
                Converters.convertTemperature(defaultMaxTemp?:0.0,holder.binding.root.context)
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