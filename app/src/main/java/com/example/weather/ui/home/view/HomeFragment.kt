package com.example.weather.ui.home.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.MyApplication
import com.example.weather.R
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.ui.home.viewmodel.HomeViewModel
import com.example.weather.ui.home.viewmodel.HomeViewModelFactory
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.Converters
import com.example.weather.utilites.NetworkManager
import com.github.matteobattilana.weather.PrecipType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var homeViewModel: HomeViewModel
    private val TAG: String = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.weatherView.setWeatherData(PrecipType.SNOW)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = HomeViewModelFactory(MyApplication.getRepository())
        homeViewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)


        updateUI()
        binding.swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(R.id.nav_home)
        }
    }

    private fun updateUI() {
        if (NetworkManager.isInternetConnected()) {
            homeViewModel.init(requireContext())
            homeViewModel.setLatitudeAndLongitude()
            getWeatherResponse()
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility=View.VISIBLE
                binding.homeScroll.visibility=View.GONE
                val oldData= homeViewModel.getOfflineWeatherData()
                if(oldData!=null){
                    homeViewModel.init(requireContext())
                    homeViewModel.setLatitudeAndLongitude()
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility=View.GONE
                    binding.homeScroll.visibility=View.VISIBLE
                    setUpHomeData(oldData)
                }else{
                    Toast.makeText(requireContext(),"${context?.getString(R.string.internet_connection)}",Toast.LENGTH_SHORT).show()
                }
            }
            Log.i(TAG, "checkConnectivity: NoConnection")
        }
    }

    private fun getWeatherResponse() {
        lifecycleScope.launch {
            homeViewModel.getCurrentWeather()
            homeViewModel.response.collectLatest {
                when (it) {
                    is RetrofitStateWeather.Loading -> {
                        binding.shimmerLayout.startShimmer()
                        binding.shimmerLayout.visibility = View.VISIBLE
                        binding.homeScroll.visibility = View.GONE
                        Toast.makeText(context, R.string.loading, Toast.LENGTH_SHORT).show()
                    }

                    is RetrofitStateWeather.OnFail -> {
                        Toast.makeText(context, it.errorMessage.message, Toast.LENGTH_SHORT).show()
                    }

                    is RetrofitStateWeather.OnSuccess -> {
                        Toast.makeText(context, R.string.done, Toast.LENGTH_SHORT).show()
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.homeScroll.visibility = View.VISIBLE
                        setUpHomeData(it.weatherResponse)
                        homeViewModel.updateWeatherData(
                            it.weatherResponse,
                            homeViewModel.getAddressFromLatLng(
                                it.weatherResponse.lat,
                                it.weatherResponse.lon
                            )
                        )
                    }

                    else -> {
                        Log.i(TAG, "getWeatherResponse: else->HomeFragment")
                    }
                }

            }
            /*homeViewModel.retrofitStateWeather.collectLatest {
            }*/
        }
    }

    private fun setUpHomeData(data: WeatherResponse) {
        Log.i(TAG, "setUpHomeData:lat-> ${data.lat} long-> ${data.lon}")
        homeViewModel.updateCityName(isAdded, data.addressName)

        Glide.with(this)
            .load(Converters.getWeatherImage(data.current?.weather?.get(0)?.icon))
            .into(binding.imageViewWeatherToday)
        setTemp(data.current?.temp)
        setWindSpeed(data.current?.wind_speed)
        setDate(data.current?.dt)
        setHumidity(data.current?.humidity)
        setCloudy(data.current?.clouds)
        setVisibility(data.current?.visibility)
        setUVI(data.current?.uvi)
        setPressure(data.current?.pressure)
        setDescription(data.current?.weather?.get(0)?.description)
        Log.i(TAG, "setUpHomeData: before city")
        homeViewModel.cityName.observe(viewLifecycleOwner) {
            Log.i(TAG, "setUpHomeData: AfterObserve $it")
            setCityName(it)
        }
        setUpDailyAdapter(data.daily?.take(7))
        setUpHourlyAdapter(data.hourly?.take(24))
    }

    private fun setDescription(description: String?) {
        Log.i(TAG, "setDescription: $description")
        if (description!!.contains("rain"))
            binding.weatherView.setWeatherData(PrecipType.RAIN)
        else if (description.contains("snow"))
            binding.weatherView.setWeatherData(PrecipType.SNOW)
        else if (description.contains("cloud"))
            binding.weatherView.setWeatherData(PrecipType.SNOW)
        else
            binding.weatherView.setWeatherData(PrecipType.CLEAR)

        binding.textViewWeatherDescription.text = description?.capitalize()
    }

    private fun setPressure(pressure: Int?) {
        Log.i(TAG, "setPressure: $pressure")
        binding.textViewPressureValue.text =
            "${Converters.convertHumidityOrPressureOrCloudy(pressure!!)} ${context?.getString(R.string.textViewPressureUnit)}"
    }

    private fun setUVI(uvi: Double?) {
        Log.i(TAG, "setUVI: $uvi")
        val temp = uvi ?: 0.0
        binding.textViewUVValue.text = Converters.convertHumidityOrPressureOrCloudy(temp.toInt())
    }

    private fun setVisibility(visibility: Int?) {
        Log.i(TAG, "setVisibility: $visibility")
        var temp = visibility
        if (temp != null) {
            if (temp > 1000) {
                temp = temp / 1000
                binding.textViewVisibilityValue.text =
                    "${Converters.convertHumidityOrPressureOrCloudy(temp)} ${context?.getString(R.string.km)}"
            } else {
                binding.textViewVisibilityValue.text =
                    Converters.convertHumidityOrPressureOrCloudy(visibility ?: 0)
            }
        }
    }

    private fun setCloudy(cloudy: Int?) {
        Log.i(TAG, "setCloudy: $cloudy")
        binding.textViewCloudyValue.text = Converters.convertHumidityOrPressureOrCloudy(cloudy ?: 0)
    }

    private fun setHumidity(humidity: Int?) {
        Log.i(TAG, "setHumidity: $humidity")
        binding.textViewHumidityValue.text =
            "${Converters.convertHumidityOrPressureOrCloudy(humidity ?: 0)} %"
    }

    private fun setTemp(temp: Double?) {
        Log.i(TAG, "setTemp: $temp")
        binding.textViewTemprature.text =
            Converters.convertTemperature(temp ?: 0.0, requireContext())
    }

    private fun setWindSpeed(speed: Double?) {
        Log.i(TAG, "setWindSpeed: $speed")
        binding.textViewWindSpeedValue.text = Converters.convertWind(speed ?: 0.0, requireContext())
    }

    private fun setDate(data: Long?) {
        Log.i(TAG, "setDate: $data")
        binding.textViewDate.text = Converters.convertDateHome(data)

    }

    private fun setCityName(cityName: String) {
        Log.i(TAG, "setCityName: $cityName")
        binding.textViewCountry.text = cityName
    }

    private fun setUpDailyAdapter(dailyList: List<DailyItem?>?) {
        val dLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        dailyAdapter = DailyAdapter(
            homeViewModel.getStringFromSharedPref(
                CONSTANTS.sharedPreferencesKeyTempUnit,
                "celsius"
            ),{
                setUVI(it.uvi)
                setPressure(it.pressure)
                setHumidity(it.humidity)
                setWindSpeed(it.wind_speed)
                setCloudy(it.clouds)
            }
        )
        binding.recyclerViewDays.apply {
            layoutManager = dLayoutManager
            adapter = dailyAdapter
        }

        dailyAdapter.submitList(
            dailyList
        )
    }

    private fun setUpHourlyAdapter(hourlyList: List<HourlyItem?>?) {
        val dlayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hourlyAdapter = HourlyAdapter(
            homeViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit, "")
        )
        binding.recyclerViewHours.apply {
            layoutManager = dlayoutManager
            adapter = hourlyAdapter
        }
        hourlyAdapter.submitList(
            hourlyList
        )
    }

}