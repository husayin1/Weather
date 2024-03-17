package com.example.weather.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID
import java.io.Serializable

data class Alerts(
    var sender_name: String? = null,
    var event: String? = null,
    var start: Int? = null,
    var end: Int? = null,
    var description: String? = null,
    var tags: List<String>
)

@Entity(tableName = "AlertsTable")
data class AlertPojo(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val lat: Double,
    val lon: Double,
    val start: Long,
    val end: Long,
    val kind: String,
)
object AlertKind {
    const val NOTIFICATION = "NOTIFICATION"
    const val ALARM = "ALARM"
}



data class LocationLatLngPojo(var type :String ,var lat:Double,var lng:Double):Serializable
