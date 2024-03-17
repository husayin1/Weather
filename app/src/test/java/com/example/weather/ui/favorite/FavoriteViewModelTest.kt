package com.example.weather.ui.favorite

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
import com.example.weather.ui.favorite.viewmodel.FavoriteViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi

@RunWith(AndroidJUnit4::class)
class FavoriteViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()
    val sL = SavedLocations(0.0, 0.0, "-", "0.0.0", 0.0, "--", 0, "---")
    val sL2 = SavedLocations(0.0, 0.0, "-", "0.0.0", 0.0, "--", 0, "---")
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
    private val local = listOf(sL, sL2)

    lateinit var repository: FakeRepository
    lateinit var viewModel: FavoriteViewModel
    lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    lateinit var fakeLocalDataSource: FakeLocalDataSource

    @Before
    fun setUp() = runBlockingTest {
        fakeRemoteDataSource = FakeRemoteDataSource(fakeWeatherData)
        fakeLocalDataSource = FakeLocalDataSource(local as MutableList<SavedLocations>)
        repository = FakeRepository(
            fakeRemoteDataSource,
            PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()),
            fakeLocalDataSource
        )
        viewModel = FavoriteViewModel(repository)
    }

    @Test
    fun favoriteViewModel_getAllSavedLocations_InsertedItems_Test() = runBlockingTest {
        //given: two savedLocations
        viewModel.addLocation(sL)
        viewModel.addLocation(sL2)
        var rep: List<SavedLocations> = emptyList()
        //when:get all savedLocations
        viewModel.getAllFavLocations().collectLatest {
            rep = it
        }
        //then:assert that the first item is the same
        MatcherAssert.assertThat(rep.get(0).address, CoreMatchers.`is`(sL.address))
        //then:assert that the saved items size is equal to the number of inserted items
        MatcherAssert.assertThat(rep.size, CoreMatchers.`is`(2))
    }
    @Test
    fun favoriteViewModel_InsertLocation_NotNullValue_Test() = runBlockingTest {
        //given: two savedLocations
        viewModel.addLocation(sL)
        var rep: List<SavedLocations> = emptyList()
        //when:get all savedLocations
        val retro = viewModel.getAllFavLocations().collectLatest {
            rep = it
        }
        //then:assert that the first item is the same
        MatcherAssert.assertThat(rep.get(0).address, CoreMatchers.`is`(sL.address))
        //then:assert that the db is not empty
        MatcherAssert.assertThat(rep.size, CoreMatchers.`is`(notNullValue()))
    }
    @Test
    fun favoriteViewModel_getAllSaved_NotNullValue_Test() = runBlockingTest {
        //given: two savedLocations
        viewModel.addLocation(sL)
        var rep: List<SavedLocations> = emptyList()
        //when:get all savedLocations
        viewModel.getAllFavLocations()

        //then:assert that the first item is the same
        MatcherAssert.assertThat(rep.get(0).address, CoreMatchers.`is`(sL.address))
        //then:assert that the db is not empty
        MatcherAssert.assertThat(rep.size, CoreMatchers.`is`(notNullValue()))
    }

    @Test
    fun favoriteViewModel_deleteSavedLocation_NullValue_Test() = runBlockingTest {
        viewModel.addLocation(sL)
        viewModel.deleteFavLocation(sL)
        var rep: List<SavedLocations> = emptyList()
        viewModel.getAllFavLocations().collectLatest {
            rep = it
        }
        //then saved locations in database should be empty
        assertThat(rep.size,`is` (0))
        //then saved locations in database does not contain insertedValue
        assertThat(rep.contains(sL),`is` (false))
    }
}
