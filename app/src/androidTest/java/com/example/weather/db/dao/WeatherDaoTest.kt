package com.example.weather.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weather.db.DataBase
import com.example.weather.model.response.Current
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.Temp
import com.example.weather.model.response.WeatherItem
import com.example.weather.model.response.WeatherResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherDaoTest {

    private lateinit var db: DataBase
    private lateinit var weatherDao: WeatherDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DataBase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = db.getWeatherDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun weatherDao_InsertWeatherResponse_DbContainOneItem() = runBlockingTest {
        //Given: WeatherData object
        val weatherData = WeatherResponse(
            current = Current(
                clouds = 0,
                humidity = 0,
                pressure = 0,
                dt = 0,
                temp = 0.0,
                wind_speed = 0.0,
                weather = listOf(
                    WeatherItem(
                        description = "0",
                        icon = "00",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                DailyItem(
                    dt = 0,
                    temp = Temp(
                        max = 0.0,
                        min = 1.1
                    ) ,
                    weather = listOf(
                        WeatherItem(
                        description = "0",
                        icon = "00",
                        id = 1
                    )
                    )
                )
            ) ,
            hourly = listOf(
                HourlyItem(
                    dt = 0,
                    weather = listOf(WeatherItem(
                        description = "00",
                        icon = "00",
                        id = 1
                    )),
                    temp = 1.1
                )
            ),
            lat = 0.0,
            lon = 0.0,
            alerts = null,
            addressName = "portfouad"
        )
        //When: insert WeatherResponse to database
        weatherDao.insertWeatherData(weatherData)
        val result = weatherDao.getWeatherDataFromDB()

        //then: assert that weatherData is inserted in the database
        Assert.assertThat(result, `is` (weatherData))
        //then assert that the element we inserted is the element in the db
        Assert.assertThat(result?.addressName, `is` (weatherData.addressName))
    }
    @Test
    fun weatherDao_DeleteWeatherResponse_DbContainNullValue() = runBlockingTest {
        //Given: WeatherData object
        val response = WeatherResponse(
            current = Current(
                clouds = 0,
                humidity = 0,
                pressure = 0,
                dt = 0,
                temp = 0.0,
                wind_speed = 0.0,
                weather = listOf(
                    WeatherItem(
                        description = "0",
                        icon = "00",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                DailyItem(
                    dt = 0,
                    temp = Temp(
                        max = 0.0,
                        min = 1.1
                    ) ,
                    weather = listOf(
                        WeatherItem(
                            description = "0",
                            icon = "00",
                            id = 1
                        )
                    )
                )
            ) ,
            hourly = listOf(
                HourlyItem(
                    dt = 0,
                    weather = listOf(WeatherItem(
                        description = "00",
                        icon = "00",
                        id = 1
                    )),
                    temp = 1.1
                )
            ),
            lat = 0.0,
            lon = 0.0,
            alerts = null,
            addressName = "portsaid"
        )
        //When: insert weatherData to database
        weatherDao.insertWeatherData(response)

        //When: delete weatherData to database
        weatherDao.deleteWeatherData(response)

        val firstWeatherData = weatherDao.getWeatherDataFromDB()?.addressName
        val result = weatherDao.getWeatherDataFromDB()
        assertThat(result, `is` (nullValue()))
        //then: assert that weatherData is not in the database
        assertThat(firstWeatherData, `is` (nullValue()) )
    }
    @Test
    fun weatherDao_UpdateWeatherResponse_DbContainsUpdatedValue()= runBlockingTest{
        //Given: WeatherData object
        val weatherData = WeatherResponse(
            current = Current(
                clouds = 0,
                humidity = 0,
                pressure = 0,
                dt = 0,
                temp = 0.0,
                wind_speed = 0.0,
                weather = listOf(
                    WeatherItem(
                        description = "0",
                        icon = "00",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                DailyItem(
                    dt = 0,
                    temp = Temp(
                        max = 0.0,
                        min = 1.1
                    ) ,
                    weather = listOf(
                        WeatherItem(
                            description = "0",
                            icon = "00",
                            id = 1
                        )
                    )
                )
            ) ,
            hourly = listOf(
                HourlyItem(
                    dt = 0,
                    weather = listOf(WeatherItem(
                        description = "00",
                        icon = "00",
                        id = 1
                    )),
                    temp = 1.1
                )
            ),
            lat = 0.0,
            lon = 0.0,
            alerts = null,
            addressName = "portsaid"
        )
        //When: insert WeatherResponse to database
        weatherDao.insertWeatherData(weatherData)
        val result = weatherDao.getWeatherDataFromDB()
        //Given:New Value
        val updatedData = WeatherResponse(
            current = Current(
                clouds = 1,
                humidity = 1,
                pressure = 1,
                dt = 1,
                temp = 1.1,
                wind_speed = 1.1,
                weather = listOf(
                    WeatherItem(
                        description = "1",
                        icon = "1",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                DailyItem(
                    dt = 1,
                    temp = Temp(
                        max = 1.1,
                        min = 1.1
                    ) ,
                    weather = listOf(
                        WeatherItem(
                            description = "1",
                            icon = "1",
                            id = 1
                        )
                    )
                )
            ) ,
            hourly = listOf(
                HourlyItem(
                    dt = 1,
                    weather = listOf(WeatherItem(
                        description = "1",
                        icon = "1",
                        id = 1
                    )),
                    temp = 1.1
                )
            ),
            lat = 1.1,
            lon = 1.1,
            alerts = null,
            addressName = "portfouad"
        )
        //When: update WeatherResponse to database
        weatherDao.updateWeatherData(updatedData)
        val updatedResult = weatherDao.getWeatherDataFromDB()

        //then: assert that weatherData is inserted in the database
        Assert.assertThat(updatedResult, `is` (updatedData))
        //then assert that the element we inserted is the element in the db
        Assert.assertThat(updatedResult?.addressName, `is` (updatedData.addressName))
    }

}