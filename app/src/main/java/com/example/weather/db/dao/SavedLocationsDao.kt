package com.example.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather.model.response.SavedLocations
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationsDao {

    @Query("SELECT * FROM SavedLocationsTable")
    fun getAllFavLocations(): Flow<List<SavedLocations>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocation(location: SavedLocations): Unit

    @Delete
    suspend fun deleteLocation(location: SavedLocations): Unit

}