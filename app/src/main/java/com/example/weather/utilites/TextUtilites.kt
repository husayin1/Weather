package com.example.weather.utilites

import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

fun getTimeFormat(timeInMilliSecond: Long): String {
    val date = Date(timeInMilliSecond)
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    return convertFormat.format(date).toString()
}
fun TextView.setTime(timeInMilliSecond: Long) {
    text = getTimeFormat(timeInMilliSecond)
}
fun getLanguageLocale(): String {
    return AppCompatDelegate.getApplicationLocales().toLanguageTags()
}
fun getADateFormat(timeInMilliSecond: Long):String{
    val pattern = "dd MMMM"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    return simpleDateFormat.format(Date(timeInMilliSecond))
}
fun getTimeFormat(timeInMilliSecond: Long,timeZone : TimeZone): String {
    val calendar = GregorianCalendar(timeZone)
    calendar.timeInMillis = timeInMilliSecond
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    convertFormat.timeZone = timeZone
    return convertFormat.format(calendar.time).toString()
}
fun TextView.setDate(timeInMilliSecond: Long){
    text = getADateFormat(timeInMilliSecond)
}
fun TextView.setTime(timeInSecond: Int?, timeZone: TimeZone) {
    text = getTimeFormat(timeInSecond?.times(1000L) ?: -1,timeZone)
}