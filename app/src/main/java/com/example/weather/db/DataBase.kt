package com.example.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather.db.dao.AlertsDao
import com.example.weather.db.dao.SavedLocationsDao
import com.example.weather.db.dao.WeatherDao
import com.example.weather.model.response.AlertPojo
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.WeatherResponse
import com.example.weather.utilites.Converters

@Database(
    entities = [SavedLocations::class, AlertPojo::class, WeatherResponse::class],
    exportSchema = false,
    version = 1
)
@TypeConverters(Converters::class)
abstract class DataBase : RoomDatabase() {
    abstract fun getSavedLocationsDao(): SavedLocationsDao
    abstract fun getAlertsDao(): AlertsDao
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: DataBase? = null
        @Synchronized
        fun getInstance(ctx: Context): DataBase {
            return instance ?: synchronized(this) {
                val INSTANCE = Room.databaseBuilder(ctx, DataBase::class.java, "weather_db")
                    .fallbackToDestructiveMigration()
                    .build()
                instance = INSTANCE
                INSTANCE
            }
        }
    }

}
