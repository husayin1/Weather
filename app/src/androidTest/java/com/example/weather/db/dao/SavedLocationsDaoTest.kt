package com.example.weather.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weather.db.DataBase
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.Temp
import com.example.weather.model.response.WeatherItem
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
@ExperimentalCoroutinesApi

class SavedLocationsDaoTest {

    private lateinit var db: DataBase
    private lateinit var savedLocationsDao: SavedLocationsDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBase::class.java
        ).allowMainThreadQueries()
            .build()
        savedLocationsDao = db.getSavedLocationsDao()

    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun savedLocationDao_InsertLocation_theSameItemInserted() = runBlockingTest {
        //Given: location to save
        val location = SavedLocations(
            0.0,
            0.0,
            "Port Said",
            "0.0.0.0",
            0.0,
            "Test",
            3,
            "0"
        )

        //When: insert location to database
        savedLocationsDao.saveLocation(location)
        val list = savedLocationsDao.getAllFavLocations()

        //then: assert that savedLocation is in the list of all Locaitons
        assertThat(list.first().get(0), `is` (location))
        //then: assert that the item is the same
        assertThat(list.first().get(0).address, `is` ("Port Said"))
        //then: assert that the db has only the inserted value
        assertThat(list.first().size, `is` (1))
    }

    @Test
    fun savedLocationDao_DeleteLocation_NullValue() = runBlockingTest {
        //Given: location
        val location = SavedLocations(
            0.0,
            0.0,
            "Test",
            "10.10.10",
            0.0,
            "Tester",
            3,
            "t"
        )


        //When: insert location to database
        savedLocationsDao.saveLocation(location)

        //When: delete location from database
        savedLocationsDao.deleteLocation(location)

        val list = savedLocationsDao.getAllFavLocations()

        //then: assert that favoriteAddress is not in the list of all favoriteAddresses
        assertThat(list.first().size, `is` (0))
        assertThat(list.first().contains(location), `is` (false))
    }

}