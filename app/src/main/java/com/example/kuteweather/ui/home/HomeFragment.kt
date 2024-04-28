package com.example.kuteweather.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.anychart.APIlib
import com.anychart.AnyChart
import com.example.kuteweather.Constants
import com.example.kuteweather.MainViewModel
import com.example.kuteweather.R
import com.example.kuteweather.api.weather.JsonProvider
import com.example.kuteweather.databinding.FragmentHomeBinding
import com.example.kuteweather.db.AppRepository
import com.example.kuteweather.tools.WeatherIcons
import com.example.kuteweather.ui.favorites.FavoritesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment(), LocationListener {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: AppRepository

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = homeViewModel
        binding.preferences = sharedViewModel
        repository = AppRepository.getInstance(requireContext())!!

        homeViewModel.selectedDay.observe(viewLifecycleOwner) {
            checkChartVisibility()
        }

        sharedViewModel.isCelsius.observe(viewLifecycleOwner) {
            checkChartVisibility()
        }

        binding.anyChartViewCelsius.setOnRenderedListener {
            if (_binding != null) // in case we left this fragment, it won't trigger nullpointerexception
                binding.progressBar2.visibility = View.GONE
        }

        initDaysText()
        initWeatherIcons()

        try {
            val args = HomeFragmentArgs.fromBundle(requireArguments())
            args.weatherArgument.let {
                //Log.d("AppDebug", "arguments for home fragment=$it")
                CoroutineScope(Dispatchers.Main).launch {
                    homeViewModel.argumentData = args.weatherArgument
                    homeViewModel.isFavorite.postValue(context?.let { it1 ->
                        AppRepository.getInstance(it1)
                    }!!.getCity(args.weatherArgument.city) != null)
                    homeViewModel.requestUrl(it.city)
                }.invokeOnCompletion { initChart() }
            }
            requireArguments().clear()
        } catch (e: Exception) {
            Log.d(Constants.DEBUG_LOG, e.message.toString())
            //fakeRequest()
            //initChart()
        }

        binding.imageviewHomeFavorite.setOnClickListener {
//            if(homeViewModel.argumentData == null) homeViewModel.argumentData =
//                FavoritesModel("", "", 0.0, 0.0)

            val city = FavoritesModel(
                homeViewModel.data.value?.cityName!!,
                homeViewModel.argumentData.country,
                homeViewModel.argumentData.latitude,
                homeViewModel.argumentData.longitude
            )

            if (homeViewModel.isFavorite.value == true) {
                homeViewModel.isFavorite.value = false
                repository.deleteCity(city)
            } else {
                homeViewModel.isFavorite.value = true
                repository.addData(city)
            }
        }

        doGeolocation()

        return binding.root
    }

    private fun checkChartVisibility() {
        // today
        if (homeViewModel.selectedDay.value == 0) {
            binding.anyChartViewDay1Celsius.visibility = View.INVISIBLE
            binding.anyChartViewDay1Fahrenheit.visibility = View.INVISIBLE
            binding.anyChartViewDay2Celsius.visibility = View.INVISIBLE
            binding.anyChartViewDay2Fahrenheit.visibility = View.INVISIBLE

            if (sharedViewModel.isCelsius.value == true) {
                binding.anyChartViewCelsius.visibility = View.VISIBLE
                binding.anyChartViewFahrenheit.visibility = View.INVISIBLE
            } else {
                binding.anyChartViewCelsius.visibility = View.INVISIBLE
                binding.anyChartViewFahrenheit.visibility = View.VISIBLE
            }
        }

        // tomorrow
        else if (homeViewModel.selectedDay.value == 1) {
            binding.anyChartViewCelsius.visibility = View.INVISIBLE
            binding.anyChartViewFahrenheit.visibility = View.INVISIBLE
            binding.anyChartViewDay2Celsius.visibility = View.INVISIBLE
            binding.anyChartViewDay2Fahrenheit.visibility = View.INVISIBLE

            if (sharedViewModel.isCelsius.value == true) {
                binding.anyChartViewDay1Celsius.visibility = View.VISIBLE
                binding.anyChartViewDay1Fahrenheit.visibility = View.INVISIBLE
            } else {
                binding.anyChartViewDay1Celsius.visibility = View.INVISIBLE
                binding.anyChartViewDay1Fahrenheit.visibility = View.VISIBLE
            }
        }

        // day after
        else if (homeViewModel.selectedDay.value == 2) {
            binding.anyChartViewCelsius.visibility = View.INVISIBLE
            binding.anyChartViewFahrenheit.visibility = View.INVISIBLE
            binding.anyChartViewDay1Celsius.visibility = View.INVISIBLE
            binding.anyChartViewDay1Fahrenheit.visibility = View.INVISIBLE

            if (sharedViewModel.isCelsius.value == true) {
                binding.anyChartViewDay2Celsius.visibility = View.VISIBLE
                binding.anyChartViewDay2Fahrenheit.visibility = View.INVISIBLE
            } else {
                binding.anyChartViewDay2Celsius.visibility = View.INVISIBLE
                binding.anyChartViewDay2Fahrenheit.visibility = View.VISIBLE
            }
        }
    }

    private fun initDaysText() {
        val date = Date()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = date
        val pattern = "EEEE dd"
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
        var weekDay = SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
        binding.textHomeDayPlus1.text = weekDay

        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1)
        weekDay = SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
        binding.textHomeDayPlus2.text = weekDay
    }

    private fun initWeatherIcons() {
        val keys = resources.getIntArray(R.array.weatherapi_keys)
        val dayIconsArray: TypedArray =
            resources.obtainTypedArray(R.array.weatherapi_day_values)
        val nightIconsArray: TypedArray =
            resources.obtainTypedArray(R.array.weatherapi_night_values)

        val max = dayIconsArray.length() - 1
        for (i in 0..max) {
            WeatherIcons.getDayMap()!![keys[i]] = dayIconsArray.getResourceId(i, 0)
            WeatherIcons.getNightMap()!![keys[i]] = nightIconsArray.getResourceId(i, 0)
        }

        dayIconsArray.recycle()
        nightIconsArray.recycle()
    }

    private fun fakeRequest() {
        if (!homeViewModel.isInitialized) {
            homeViewModel.loadFrom(JsonProvider.json1)
            homeViewModel.isInitialized = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    override fun onLocationChanged(location: Location) {
        Log.d(Constants.DEBUG_LOG, "lat=${location.latitude}, long=${location.longitude}")

        // Request to API
        //doRequest(location.latitude, location.longitude, sharedViewModel.lang)
        CoroutineScope(Dispatchers.Main).launch {
            homeViewModel.requestUrl("${location.latitude},${location.longitude}")
        }.invokeOnCompletion { initChart() }

        // Stop updating
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(this)
    }

    @SuppressLint("MissingPermission")
    private fun doGeolocation() {

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            1
        )

        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        //locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER,null,application.mainExecutor,            Consumer { Log.d(Constants.DEBUG_LOG, "lat=${it.latitude}, long=${it.longitude}") })
    }

    override fun onProviderDisabled(provider: String) {
        //super.onProviderDisabled(provider)
    }

    override fun onProviderEnabled(provider: String) {
        //super.onProviderEnabled(provider)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        //Log.d(Constants.DEBUG_LOG, "onStatusChanged: depecrated. Doing nothing")
    }

    private fun initChart() {

        if(_binding == null) {
            return;
        }
        //homeViewModel.isLoading.value = true
        binding.progressBar2.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main.immediate).launch {
            Log.d("AppDebug", "Setting Charts")

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewCelsius)
            var chartCelsius = AnyChart.line()
            chartCelsius.data(homeViewModel.data.value?.array_tempCelsius)
            chartCelsius.title("T°C")
            binding.anyChartViewCelsius.setChart(chartCelsius)

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewFahrenheit)
            var chartFahrenheit = AnyChart.line()
            chartFahrenheit.data(homeViewModel.data.value?.array_tempFahrenheit)
            chartFahrenheit.title("T°F")
            binding.anyChartViewFahrenheit.setChart(chartFahrenheit)

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewDay1Celsius)
            chartCelsius = AnyChart.line()
            chartCelsius.data(homeViewModel.weatherOfTheWeek[1]?.array_tempCelsius)
            chartCelsius.title("T°C")
            binding.anyChartViewDay1Celsius.setChart(chartCelsius)

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewDay1Fahrenheit)
            chartFahrenheit = AnyChart.line()
            chartFahrenheit.data(homeViewModel.weatherOfTheWeek[1]?.array_tempFahrenheit)
            chartFahrenheit.title("T°F")
            binding.anyChartViewDay1Fahrenheit.setChart(chartFahrenheit)

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewDay2Celsius)
            chartCelsius = AnyChart.line()
            chartCelsius.data(homeViewModel.weatherOfTheWeek[2]?.array_tempCelsius)
            chartCelsius.title("T°C")
            binding.anyChartViewDay2Celsius.setChart(chartCelsius)

            APIlib.getInstance().setActiveAnyChartView(binding.anyChartViewDay2Fahrenheit)
            chartFahrenheit = AnyChart.line()
            chartFahrenheit.data(homeViewModel.weatherOfTheWeek[2]?.array_tempFahrenheit)
            chartFahrenheit.title("T°F")
            binding.anyChartViewDay2Fahrenheit.setChart(chartFahrenheit)
        }
    }

}