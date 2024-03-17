package com.example.weather.ui.home

import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.ui.home.viewmodel.HomeViewModel
import com.example.weather.utilites.CONSTANTS
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    lateinit var homeViewModel: HomeViewModel
    lateinit var fakeRepository:FakeRepository
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
        homeViewModel = HomeViewModel(fakeRepository)

    }

    @Test
    fun homeViewModel_getCurrentWeatherData_notNullValue_Test()= runBlockingTest{
        //given home fragment is initialized with internetConnection
        fakeRepository.putStringInSharedPref(CONSTANTS.sharedPreferencesKeyLanguage,CONSTANTS.english)
        //when trying to get HomeData
        var data = fakeWeatherData
        //then its not null
        val rp = homeViewModel.response.first()
        when(rp){
            is RetrofitStateWeather.OnSuccess->{
                data=rp.weatherResponse
            }
            is RetrofitStateWeather.OnFail->{

            }
            is RetrofitStateWeather.Loading->{

            }
        }
        assertThat(data, `is` (notNullValue()))
    }
    @Test
    fun homeViewModel_updateWeatherData_updatedData_Test()= runBlockingTest {
        //given home data is initialized for first time
        val updateData=WeatherResponse(
            null,
            null,
            null,
            0.0,
            null,
            0.0,
            "port said",
        )
        //when internet is disconnected
        //then given value is the update value from last time he was online
        homeViewModel.updateWeatherData(updateData,"port said")
        val offlineData =fakeRepository.getWeatherDataFromDB()
        //then: assert that the updated data is retrieved
        assertThat(offlineData, `is` (updateData))
    }

    @Test
    fun homeViewModel_getCurrentWeather_Test()= runBlockingTest{
        //given fake response
        homeViewModel.getCurrentWeather()
    }


    @Test
    fun homeViewModel_updateHomeWeatherData_notTheSame_Test()= runBlockingTest {
        //given getting offline data before updating
        val beforeUpdate=WeatherResponse(
            null,
            null,
            null,
            0.0,
            null,
            0.0,
            "portsaid",
        )
        homeViewModel.updateWeatherData(beforeUpdate,"portsaid")
        val old= homeViewModel.getOfflineWeatherData()
        //when updating weather data
        val updateData=WeatherResponse(
            null,
            null,
            null,
            0.0,
            null,
            0.0,
            "",
        )
        homeViewModel.updateWeatherData(updateData,"")
        val new = homeViewModel.getOfflineWeatherData()
        //then the old value not equal updated value
        assertNotEquals(old,new)
    }



}
