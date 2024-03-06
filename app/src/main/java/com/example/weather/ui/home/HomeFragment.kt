package com.example.weather.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weather.MainActivity
import com.example.weather.R
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.features.home.DailyAdapter
import com.example.weather.features.home.HourlyAdapter
import com.example.weather.model.response.DailyItem
import com.example.weather.model.response.HourlyItem
import com.example.weather.model.response.WeatherResponse
import com.example.weather.network.RetrofitStateWeather
import com.example.weather.utilites.CONSTANTS
import com.example.weather.utilites.Converters
import com.example.weather.utilites.Helper
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyAdapter:HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var homeViewModel:HomeViewModel
    private val TAG:String = "HomeFragment"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.weatherView.setWeatherData(PrecipType.CLEAR)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelFactory = HomeViewModelFactory(MainActivity.getInstanceRepository())
        homeViewModel= ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

        getWeatherResponse()
        binding.swipeRefreshLayout.setOnRefreshListener {
            findNavController().navigate(R.id.nav_home)
        }
    }

    private fun getWeatherResponse(){
        lifecycleScope.launch {
            homeViewModel.getCurrentWeather()
            homeViewModel.retrofitStateWeather.collectLatest {
                when(it){
                    is RetrofitStateWeather.Loading ->{
                        Toast.makeText(context,"Loading",Toast.LENGTH_SHORT).show()
                    }
                    is RetrofitStateWeather.OnFail ->{
                        Toast.makeText(context,it.errorMessage.message,Toast.LENGTH_SHORT).show()
                    }
                    is RetrofitStateWeather.OnSuccess ->{
                        setUpHomeData(it.weatherResponse)
                    }
                    else ->{
                        Log.i(TAG, "getWeatherResponse: HomeFragment")
                    }
                }
            }
        }
    }
    fun setUpHomeData(data:WeatherResponse){
        Log.i(TAG, "setUpHomeData: ${data.current?.wind_speed} ${data.current?.humidity}")
        Glide.with(this)
            .load(Helper.getWeatherImage(data.current?.weather?.get(0)?.icon))
            .into(binding.imageViewWeatherToday)
        setTemp(data.current?.temp)
        setWindSpeed(data.current?.wind_speed)
        setDate(data.current?.dt)
        setHumidity(data.current?.humidity)
        setCloudy(data.current?.clouds)
        setVisibility(data.current?.visibility)
        setUVI(data.current?.uvi)
        setPressure(data.current?.pressure)
        setDescription(data.current?.weather?.get(0)?.main)
        homeViewModel.cityName.observe(viewLifecycleOwner){
            setCityName(it)
        }
        setUpDailyAdapter(data.daily)
        setUpHourlyAdapter(data.hourly)
    }
    private fun setDescription(description:String?){
        if(description=="Rain")
            binding.weatherView.setWeatherData(PrecipType.CLEAR)
        else if(description=="Snow")
            binding.weatherView.setWeatherData(PrecipType.CLEAR)
        else
            binding.weatherView.setWeatherData(PrecipType.CLEAR)

        binding.textViewWeatherDescription.text = description?.capitalize()
    }
    private fun setPressure(pressure:Int?){
        binding.textViewPressureValue.text=Converters.convertHumidityOrPressureOrCloudy(pressure!!)
    }
    private fun setUVI(uvi:Int?){
        binding.textViewUVValue.text = uvi.toString()
    }
    private fun setVisibility(visibility:Int?){
        binding.textViewVisibilityValue.text = visibility.toString()
    }
    private fun setCloudy(cloudy:Int?){
        binding.textViewCloudyValue.text= Converters.convertHumidityOrPressureOrCloudy(cloudy!!)
    }

    private fun setHumidity(humidity:Int?){
        Log.i(TAG, "setHumidity: $humidity")
        binding.textViewHumidityValue.text = Converters.convertHumidityOrPressureOrCloudy(humidity!!)
    }
    private fun setTemp(temp: Double?) {
        binding.textViewTemprature.text =
            Converters.convertTemperature(temp!!, requireContext())
    }

    private fun setWindSpeed(speed: Double?) {
        Log.i(TAG, "setWindSpeed: $speed")
        binding.textViewWindSpeedValue.text = Converters.convertWind(speed!!, requireContext())
    }
    private fun setDate(data:Long?){
        binding.textViewDate.text = Converters.convertDateHome(data)

    }
    private fun setCityName(cityName:String){
        binding.textViewCountry.text = cityName
    }
    private fun setUpDailyAdapter(dailyList:List<DailyItem?>?){
        val dlayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        dailyAdapter = DailyAdapter(
            homeViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit, ""),
        )
        binding.recyclerViewDays.apply {
            layoutManager = dlayoutManager
            adapter = dailyAdapter
        }

        dailyAdapter.submitList(
            dailyList
        )
    }
    private fun setUpHourlyAdapter(hourlyList:List<HourlyItem?>?){
        val dlayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        hourlyAdapter = HourlyAdapter(
            homeViewModel.getStringFromSharedPref(CONSTANTS.sharedPreferencesKeyTempUnit,"")
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