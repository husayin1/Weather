package com.example.weather.ui.alerts


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.AlertItemBinding
import com.example.weather.model.response.AlertPojo
import com.example.weather.utilites.setDate
import com.example.weather.utilites.setTime

class AlertsAdapter(private val removeClickListener: RemoveClickListener) : ListAdapter<AlertPojo, AlertsAdapter.ViewHolder>(DiffUtils) {
    class ViewHolder(val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alertWeatherData = getItem(position)

        holder.binding.imageViewDelete.setOnClickListener{
            removeClickListener.onRemoveClick(alertWeatherData)
        }

        holder.binding.textViewEndDate.setDate(alertWeatherData.end)
        holder.binding.textViewEndTime.setTime(alertWeatherData.end)

    }



    object DiffUtils : DiffUtil.ItemCallback<AlertPojo>() {
        override fun areItemsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem == newItem
        }

    }

    class RemoveClickListener(val removeClickListener : (AlertPojo) -> Unit){
        fun onRemoveClick(alertEntity: AlertPojo) = removeClickListener(alertEntity)
    }
}
