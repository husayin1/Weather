package com.example.weather.ui.startup

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.db.FakeLocalDataSource
import com.example.weather.model.repository.FakeRepository
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.Temp
import com.example.weather.model.response.WeatherItem
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.FakeRemoteDataSource
import com.example.weather.ui.startup.viewmodel.StartUpViewModel
import com.example.weather.utilites.CONSTANTS
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartUpViewModelTest {
    lateinit var startUpViewModel: StartUpViewModel
    lateinit var fakeRepository: FakeRepository
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var fakeLocalDataSource: FakeLocalDataSource

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

        startUpViewModel = StartUpViewModel(fakeRepository)
    }


    @Test
    fun checkForPuttingStringInSharedPrefTest() {
        //Given: StartUpViewModel is initialized
        //when: puttingStringInSharedPref
        val key = "temperature_unit"
        val value = "celsius"
        startUpViewModel.putStringInSharedPref(key, value)

        // Retrieve the value from shared preferences
        val retrievedValue = fakeRepository.getStringFromSharedPref(key, "no")

        //then: value from sharedPref should equal the input
        assertThat(retrievedValue, `is`(value))
    }

    @Test
    fun checkForPuttingBooleanInSharedPref() {
        //Given: StartUpViewModel is initialized
        //when: puttingBooleanInSharedPref

        val key = "is_not_first_time"
        val value = true
        startUpViewModel.putBooleanInSharedPref(key, value)
        // Retrieve the value from shared preferences
        val retrievedValue = fakeRepository.getBooleanFromSharedPref(key, false)

        //then: value of key from shared preferences should be equal to booleanInput
        assertThat(retrievedValue, `is`(value))

    }

    @Test
    fun checkNightModeTest() {
        //Given: StartUpViewModel is initialized
        //when: putNighMode if users' device in night mode
        startUpViewModel.setIsNightMode()


        // Retrieve the value from shared preferences
        val retrievedValue = fakeRepository.getBooleanFromSharedPref(CONSTANTS.setIsNightMode, false)
        //then: value of setIsNightMode from shared preferences should be equal to true
        assertThat(retrievedValue, `is`(true))

    }
}
