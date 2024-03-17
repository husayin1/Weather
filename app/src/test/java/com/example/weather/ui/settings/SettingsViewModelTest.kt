package com.example.weather.ui.settings

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
import com.example.weather.ui.settings.viewmodel.SettingsViewModel
import com.example.weather.utilites.CONSTANTS
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {
    lateinit var settingsViewModel: SettingsViewModel
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
        settingsViewModel = SettingsViewModel(fakeRepository)
    }
    @Test
    fun settingsViewModel_putStringInSharedPref_Test() {
        //Given: settings view model is initialized
        //when: putting settings in shared pref
        val key = CONSTANTS.sharedPreferencesKeyWindSpeedUnit
        val value = CONSTANTS.mph
        settingsViewModel.putStringInSharedPref(key, value)

        //then: the value of unit is equal to the value from shared pref
        Assert.assertEquals(value, fakeRepository.getStringFromSharedPref(key, ""))
    }

    @Test
    fun settingsViewModel_putBooleanInSharedPrefTest() {
        //Given: SettingsViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = CONSTANTS.setIsNightMode
        val value = false
        settingsViewModel.putBooleanInSharedPref(key, value)

        //then: value of key from shared preferences should be equal to value
        Assert.assertEquals(value, fakeRepository.getBooleanFromSharedPref(key, false))
    }

    @Test
    fun settingViewModel_checkIsDarkSharedPref_Test() {
        //Given: SettingsViewModel is initialized
        //when: check if user uses night mode

        //then: value in shared preferences should be false
        Assert.assertEquals(false, settingsViewModel.isDark())
    }
}
