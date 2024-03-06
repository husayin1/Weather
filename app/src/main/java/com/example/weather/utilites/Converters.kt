package com.example.weather.utilites

import android.content.Context
import android.preference.PreferenceManager
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.model.response.WeatherResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Converters {
    companion object{
        fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        fun kelvinToFahrenheit(temp: Double): Double {
            return temp * 9/5 - 459.67
        }

        fun meterPerSecondToMilePerHour(speed: Double): Double {
            return speed * 2.237
        }

        fun convertTemperature(temp: Double, context: Context): String {

            if (MainActivity.getInstanceSharedPreferences().getString("language", "")=="arabic"){

                    return when (MainActivity.getInstanceSharedPreferences().getString("temperature_unit", "")){
                        "celsius" -> "${convertToArabicNumber(kelvinToCelsius(temp).toInt().toString())} °C"
                        "fahrenheit" -> "${convertToArabicNumber(kelvinToFahrenheit(temp).toInt().toString())} °F"
                        else -> "${convertToArabicNumber((temp).toInt().toString())} °K"

                }
            } else{
                return when (MainActivity.getInstanceSharedPreferences().getString("temperature_unit", "")){
                    "celsius" -> "${(kelvinToCelsius(temp).toInt().toString())} °C}"
                    "fahrenheit" -> "${(kelvinToFahrenheit(temp).toInt().toString())} °F}"
                    else -> "${((temp).toInt().toString())} °K"
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
            val formatDate = SimpleDateFormat("dd MMM - yyyy - EEE", Locale.getDefault())
            formatDate.timeZone = TimeZone.getTimeZone("GMT+2")

            val formatTime = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formatTime.timeZone = TimeZone.getTimeZone("GMT+2")
            return "${formatDate.format(date)} | ${formatTime.format(date)}"
        }

        fun convertWind(wind: Double, context: Context): String {
            if (MainActivity.getInstanceSharedPreferences().getString("language", "")=="arabic"){
                return when (MainActivity.getInstanceSharedPreferences().getString("wind_speed_unit", "")){
                    "mph" -> "${convertToArabicNumber(meterPerSecondToMilePerHour(wind).toInt().toString())} mph"
                    else -> "${convertToArabicNumber((wind).toInt().toString())} m/s"
                }
            } else{
                return when (MainActivity.getInstanceSharedPreferences().getString("wind_speed_unit", "")){
                    "mph" -> "${(meterPerSecondToMilePerHour(wind).toInt().toString())} mph"
                    else -> "${((wind).toInt().toString())} Miles/Hour"
                }
            }

        }

        fun convertHumidityOrPressureOrCloudy(input: Int): String {
            if (MainActivity.getInstanceSharedPreferences().getString("language", "")=="arabic"){
                return convertToArabicNumber(input.toString())
            } else{
                return input.toString()
            }
        }

        fun convertToArabicNumber(englishNumberInput: String): String {
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
    }
}