package com.example.weather.ui.splash

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
import com.example.weather.ui.splash.viewmodel.SplashViewModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashViewModelTest {
    lateinit var splashViewModel: SplashViewModel
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
        splashViewModel = SplashViewModel(fakeRepository)
    }
    @Test
    fun checkIfFirstTimeUsingAppTest(){
        //given:splashViewModel is initialized
        //when :user open the application for first time
        //then: value should be false
        assertThat(splashViewModel.isNotFirstTime(), `is`(false))
    }
    @Test
    fun checkIfUserThemeWasDarkTest(){
        //given: splashViewModel is initialized
        //when user open the application with light mode
        //then: value should be false
        assertThat(splashViewModel.isDark(), `is`(false))

    }

}
