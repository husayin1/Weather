package com.example.weather.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.response.AlertPojo

@Dao
interface AlertsDao {

    @Query("SELECT * FROM ALERTSTABLE")
    fun getAllAlerts(): LiveData<List<AlertPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertPojo): Unit

    @Delete
    suspend fun deleteAlert(alert: AlertPojo): Unit

    @Query("select * from ALERTSTABLE where id = :id limit 1")
    fun getAlertWithId(id: String): AlertPojo
}