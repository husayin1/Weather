package com.example.weather.ui.location

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.db.FakeLocalDataSource
import com.example.weather.model.repository.FakeRepository
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.Temp
import com.example.weather.model.response.WeatherItem
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.FakeRemoteDataSource
import com.example.weather.ui.home.viewmodel.HomeViewModel
import com.example.weather.ui.location.viewmodel.LocationViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class LocationViewModelTest {

    lateinit var fakeRepository:FakeRepository
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var locationViewModel:LocationViewModel

    val fakeWeatherData = WeatherResponse(
        current = Current(
            clouds = 0,
            humidity = 0,
            pressure = 0,
            dt = 0,
            temp = 0.0,
            wind_speed = 0.0,
            weather = listOf(
                WeatherItem(
                    description = "-",
                    icon = "--",
                    id = 1
                )
            )
        ),
        daily = listOf(
            DailyItem(
                dt = 0,
                temp = Temp(
                    max = 0.0,
                    min = 0.0
                ),
                weather = listOf(
                    WeatherItem(
                        description = "-",
                        icon = "--",
                        id = 1
                    )
                )
            )
        ),
        hourly = listOf(
            HourlyItem(
                dt = 0,
                weather = listOf(
                    WeatherItem(
                        description = "-",
                        icon = "--",
                        id = 1
                    )
                ),
                temp = 0.0
            )
        ),
        lat = 0.0,
        lon = 0.0,
        alerts = null,
        addressName = null
    )

    @Before

    fun setUp(){
        fakeRemoteDataSource = FakeRemoteDataSource(fakeWeatherData)
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRepository = FakeRepository(
            fakeRemoteDataSource,
            PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()),
            fakeLocalDataSource
        )
        locationViewModel = LocationViewModel(fakeRepository)

    }

    @Test
    fun locationViewModel_saveLocationFromMap_insertedItem_Test()=runBlockingTest{
        //given:location to save
        val savedLocations=SavedLocations(
            0.0,
            0.0,
            "unkown",
            "0.0.0.0",
            0.0,
            "-",
            0,
            "0"
        )
        //when insert item
        locationViewModel.insertToFavorite(savedLocations)
        var rep: List<SavedLocations> = emptyList()
        fakeRepository.getAllSavedLocations().collectLatest {
            rep=it
        }
        //then: inserted item is the same
        assertEquals(rep.get(0).address,savedLocations.address)
    }

}
