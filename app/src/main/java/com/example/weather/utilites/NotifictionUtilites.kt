package com.example.weather.utilites

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import com.example.weather.R

import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat.getString
import java.util.Locale

private const val CHANNEL_ID = "my_channel_id"
fun sendNotification(context: Context, message:String) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    val notification = Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(context,R.string.app_name))
        .setContentText(message)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_ALL)
        .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
        .setAutoCancel(true)
        .build()

    notificationManager.notify(0, notification)
}
 fun setAddressFromLatAndLon(context:Context,latitude: Double, longitude: Double):String {
     var geocoder = Geocoder(context, Locale.getDefault())
     var addressName:String =""
     val addresses = geocoder.getFromLocation(latitude, longitude, 1)
     if (addresses != null && addresses.isNotEmpty()) {
         val address = addresses[0]
         addressName =
             address.locality ?: address.adminArea ?: "${R.string.unknown}"
     }
     return addressName
 }
