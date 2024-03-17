package com.example.weather.utilites

import android.content.Context
import androidx.room.TypeConverter
import com.example.weather.MainActivity
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.model.response.Alerts
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Converters {
    companion object{
        private fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        private fun kelvinToFahrenheit(temp: Double): Double {
            return temp * 9/5 - 459.67
        }

        private fun meterPerSecondToMilePerHour(speed: Double): Double {
            return speed * 2.237
        }

        fun convertTemperature(temp: Double, context: Context): String {

            return if (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyLanguage, CONSTANTS.english)==CONSTANTS.arabic){

                when (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyTempUnit, CONSTANTS.kelvin)){
                    "celsius" -> "${convertToArabicNumber(kelvinToCelsius(temp).toInt().toString())} ${context.getString(R.string.celsius_unit)}"
                    "fahrenheit" -> "${convertToArabicNumber(kelvinToFahrenheit(temp).toInt().toString())} ${context.getString(R.string.fahrenheit_unit)}"
                    else -> "${convertToArabicNumber((temp).toInt().toString())} ${context.getString(R.string.kelvin_unit)}"

                }
            } else{
                when (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyTempUnit, CONSTANTS.kelvin)){
                    "celsius" -> "${(kelvinToCelsius(temp).toInt().toString())} ${context.getString(R.string.celsius_unit)}"
                    "fahrenheit" -> "${(kelvinToFahrenheit(temp).toInt().toString())} ${context.getString(R.string.fahrenheit_unit)}"
                    else -> "${((temp).toInt().toString())} ${context.getString(R.string.kelvin_unit)}"
                }
            }
        }

        fun convertDate(data:Long?):String{
            val date = Date(data!!*1000L)
            val format = SimpleDateFormat("EEE", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("GMT+2")
            }
            return format.format(date)

        }
        fun convertHour(data:Long?):String{
            val date = Date(data!!*1000L)
            val format =SimpleDateFormat("h:mm a", Locale.getDefault()).apply{
                timeZone = TimeZone.getTimeZone("GMT+2")
            }
            return format.format(date)

        }
        fun convertDateHome(data:Long?):String{
            val date = Date(data!! * 1000L)
            val formatDate = SimpleDateFormat("dd MMM yyyy - EEE", Locale.getDefault())
            formatDate.timeZone = TimeZone.getTimeZone("GMT+2")

            val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formatTime.timeZone = TimeZone.getTimeZone("GMT+2")
            return "${formatDate.format(date)} | ${formatTime.format(date)}"
        }

        fun convertWind(wind: Double, context: Context): String {
            return if (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.english)==CONSTANTS.arabic){
                when (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyWindSpeedUnit, CONSTANTS.mps)){
                    CONSTANTS.mph -> "${convertToArabicNumber(meterPerSecondToMilePerHour(wind).toInt().toString())} ${context.getString(R.string.mph)}"
                    else -> "${convertToArabicNumber((wind).toInt().toString())} ${context.getString(R.string.mps)}"
                }
            } else{
                when (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyWindSpeedUnit, CONSTANTS.mps)){
                    CONSTANTS.mph -> "${(meterPerSecondToMilePerHour(wind).toInt().toString())} ${context.getString(R.string.mph)}"
                    else -> "${((wind).toInt().toString())} ${context.getString(R.string.mps)}"
                }
            }

        }

        fun convertHumidityOrPressureOrCloudy(input: Int): String {
            if (MyApplication.getSharedPref().getString(CONSTANTS.sharedPreferencesKeyLanguage, "")==CONSTANTS.arabic){
                return convertToArabicNumber(input.toString())
            } else{
                return input.toString()
            }
        }

        private fun convertToArabicNumber(englishNumberInput: String): String {
            val arabicNumbers = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
            val builder = StringBuilder()
            for (i in englishNumberInput) {
                if (englishNumbers.contains(i)) {
                    builder.append(arabicNumbers[englishNumbers.indexOf(i)])
                } else {
                    builder.append(i) // point
                }
            }
            return builder.toString()
        }
        fun getWeatherImage(icon: String?): Int {
            when (icon){
                "01d" -> return R.drawable.i01d
                "02d" -> return R.drawable.i02d
                "03d" -> return R.drawable.i03d
                "04d" -> return R.drawable.i04d
                "09d" -> return R.drawable.i09d
                "10d" -> return R.drawable.i10d
                "11d" -> return R.drawable.i11d
                "13d" -> return R.drawable.i13d
                "50d" -> return R.drawable.i50d
                "01n" -> return R.drawable.i01n
                "02n" -> return R.drawable.i02n
                "03n" -> return R.drawable.i03n
                "04n" -> return R.drawable.i04n
                "09n" -> return R.drawable.i09n
                "10n" -> return R.drawable.i10n
                "11n" -> return R.drawable.i11n
                "13n" -> return R.drawable.i13n
                "50n" -> return R.drawable.i50n
                else -> return R.drawable.baseline_add_alert_24
            }
        }
    }
    @TypeConverter
    fun fromJsonCurrent(value: String): Current {
        return Gson().fromJson(value, Current::class.java)
    }

    @TypeConverter
    fun toJsonCurrent(value: Current): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJsonDailyList(value: String): List<DailyItem> {
        val listType = object : TypeToken<List<DailyItem>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonDailyList(list: List<DailyItem>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonHourlyList(value: String): List<HourlyItem> {
        val listType = object : TypeToken<List<HourlyItem>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonHourlyList(list: List<HourlyItem>): String {
        return Gson().toJson(list)
    }
    @TypeConverter
    fun fromJsonAlertList(value: String): List<Alerts>? {
        return if (value.isNullOrEmpty()) {
            null
        } else {
            val listType = object : TypeToken<List<Alerts>>() {}.type
            Gson().fromJson(value, listType)
        }
    }

    @TypeConverter
    fun toJsonAlertList(list: List<Alerts>?): String {
        return list?.let { Gson().toJson(it) } ?: ""
    }

}