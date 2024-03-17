package com.example.weather.db

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weather.model.response.SavedLocations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {
    @get:Rule
    var instantExecutionRule = InstantTaskExecutorRule()
    lateinit var dataBase: DataBase
    lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp(){
        dataBase= Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),DataBase::class.java
        ).allowMainThreadQueries().build()
        localDataSource=LocalDataSource.getInstance(ApplicationProvider.getApplicationContext())
    }

    @After
    fun closeDb(){
        dataBase.close()
    }


    @Test
    fun deleteWeatherData()= runBlockingTest {
        //given: weatherResponse
        val savedLocation=SavedLocations(0.0,0.0,"portsaid","0.0",0.0,"0",0,"0")
        //when insert element in db
        localDataSource.deleteLocation(savedLocation)
        dataBase.getSavedLocationsDao().deleteLocation(savedLocation)
        val result=localDataSource.getAllSavedLocations().first()
        assertThat(result.isEmpty(), `is` (true))
    }


    @Test
    fun insertWeatherData()= runBlockingTest {
        //given: weatherResponse
        val savedLocation=SavedLocations(0.0,0.0,"portsaid","0.0",0.0,"0",0,"0")
        //when insert element in db
        localDataSource.saveLocation(savedLocation)
        dataBase.getSavedLocationsDao().saveLocation(savedLocation)
        val result=localDataSource.getAllSavedLocations().first()
        assertThat(result.isEmpty(), `is` (false))
    }

    @Test
    fun getAllSavedLocation()=runBlockingTest {
        //given 2 locations to save
        val savedLocation2=SavedLocations(1.1,1.1,"portfouad","0.0",0.0,"0",0,"0")
        //when call
        localDataSource.saveLocation(savedLocation2)
        //then
        dataBase.getSavedLocationsDao().saveLocation(savedLocation2)

        val result =localDataSource.getAllSavedLocations().first()

        assertThat(result.size,`is` (1))
        assertThat(result, hasItem(savedLocation2))
    }

}