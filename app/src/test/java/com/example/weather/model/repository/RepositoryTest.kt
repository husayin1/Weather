package com.example.weather.model.repository

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weather.db.FakeLocalDataSource
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.SavedLocations
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.Temp
import com.example.weather.model.response.WeatherItem
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.FakeRemoteDataSource
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matchers.equalTo
import org.jetbrains.annotations.NotNull
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {
    val localDataSourceLocations = mutableListOf(
        SavedLocations(0.0, 0.0, "-", "0.0.0", 0.0, "--", 0, "---")
    )
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
        addressName = "port said"
    )
    val secondWeatherData = WeatherResponse(
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
        addressName = "port fouad"
    )
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var fakeLocalDataSource: FakeLocalDataSource
    lateinit var repository: FakeRepository
    lateinit var result: WeatherResponse
    lateinit var resp:List<SavedLocations>

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeRemoteDataSource(fakeWeatherData)
        fakeLocalDataSource = FakeLocalDataSource(localDataSourceLocations)
        repository = FakeRepository(
            fakeRemoteDataSource,
            PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()),
            fakeLocalDataSource
        )
    }

    @Test
    fun network_getWeatherResponseOverNetwork_weatherResponse_Test() = runBlockingTest {
        //Given: WeatherResponse object

        //when: getCurrentWeatherDataOverNetwork
        repository.getCurrentWeatherData(0.0, 0.0, CONSTANTS.english).collectLatest {
            result = it
        }
        //then: assert that weatherResponse is returned
        Assert.assertThat(result, `is`(fakeWeatherData))
        //then: assert That the data is the same
        Assert.assertThat(result.addressName,`is` (fakeWeatherData.addressName))
    }

    @Test
    fun dataBase_getUpdatedWeatherResponseLocally_storedResponse_Test() = runBlockingTest {
        repository.updateWeatherData(fakeWeatherData)
        val result = repository.getWeatherDataFromDB()
        //then: assert that weatherData is that inserted in the database
        Assert.assertThat(result, `is`(fakeWeatherData))
        //then: assert that the items is the same
        Assert.assertThat(result.addressName, `is` (fakeWeatherData.addressName))

    }
    @Test
    fun dataBase_getWeatherResponseLocally_OnlyOneItem_Test() = runBlockingTest {
        //given: two weather response
        repository.updateWeatherData(fakeWeatherData)
        repository.updateWeatherData(secondWeatherData)
        //when: getting weather response from the database
        val result = repository.getWeatherDataFromDB()
        //then: assert that weatherData is that inserted in the database
        Assert.assertThat(result, `is`(secondWeatherData))
        //then: assert that the items is the same
        Assert.assertThat(result.addressName, not (equalTo(fakeWeatherData.addressName)))
        //then: assert that the item in the db is only 1 value
        Assert.assertThat(result, `is` (notNullValue()))
    }


    @Test
    fun repository_GetAllSavedLocation_theItemsInserted_Test() = runBlockingTest {
        //given:saved Location
        val location=SavedLocations(0.0, 0.0, "-", "1.1.1", 0.0, "--", 0, "---")
        val location2=SavedLocations(0.0, 0.0, "-", "2.2.2", 0.0, "--", 0, "---")
        repository.saveLocation(location)
        repository.saveLocation(location2)
        //when: get All savedLocations
        repository.getAllSavedLocations().collectLatest {
            resp=it
        }
        //then: assert that savedLocation is inserted in the database
        Assert.assertThat(resp.get(0).latLngString, `is`(location.latLngString))
        //then:assert that savedLocations is the onlyOneItem
        Assert.assertThat(resp.size,`is`(2))

    }

    @Test
    fun repository_insertSavedLocationTest_InsertedItem_Test() = runBlockingTest {
        //given: SavedLocation
        val location=SavedLocations(0.0, 0.0, "-", "0.0.0", 5.5, "--", 0, "---")
        //When: insert savedLocation to database
        repository.saveLocation(location)
        repository.getAllSavedLocations().collectLatest {
            resp=it
        }
        //then: assert that location is inserted in the database
        Assert.assertThat(resp.size, `is`(1))
        //then: assert that location is inserted in the database is the same
        Assert.assertThat(resp.get(0).currentTemp, `is`(location.currentTemp))
    }

    @Test
    fun repository_deleteSavedLocation_NullValue_Test() = runBlockingTest {
        //When: insert savedLocation to database
        val location = SavedLocations(0.0, 0.0, "-", "0.0.0", 0.0, "--", 0, "---")
        repository.saveLocation(location)

        //When: delete savedLocation from database
        repository.deleteLocation(location)
        repository.getAllSavedLocations().collectLatest {
            resp = it
        }
        //then: assert that savedLocation is deleted from the database
        Assert.assertThat(resp.size, `is`(0))
        //then: assert that the item is not contained
        Assert.assertThat(resp.contains(location), `is`(false))
    }


}