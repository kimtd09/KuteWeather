package com.example.kuteweather.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.kuteweather.api.weather.ContentProvider
import com.example.kuteweather.model.WeatherGson1
import com.example.kuteweather.model.WeatherModel
import com.example.kuteweather.tools.DateConverter
import com.example.kuteweather.tools.WeatherIcons
import com.example.kuteweather.ui.favorites.FavoritesModel

class HomeViewModel : ViewModel() {

    /*
        Store weather for today, tomorrow and day after
     */
    var weatherOfTheWeek: ArrayList<WeatherModel?> = arrayListOf(null, null, null)

    private var _data = MutableLiveData(weatherOfTheWeek[0])
    private var _selectedDay = MutableLiveData(0)
    private var _isKph = MutableLiveData(true)
    private var _lang = MutableLiveData("en")
    private var _isFavorite = MutableLiveData(false)

    val data: LiveData<WeatherModel?> = _data
    val selectedDay: LiveData<Int> = _selectedDay
    val lang: LiveData<String> = _lang
    var isInitialized = false
    val isFavorite = _isFavorite

    //var dayIconsMapping: HashMap<Int, Int>? = null
    //var nightIconsMapping: HashMap<Int, Int>? = null
    var argumentData = FavoritesModel("", "", 0.0, 0.0)

    fun changeSelected(selectedDay: Int) {
        // TODO implement with arraylist
        if (_selectedDay.value == selectedDay) return

        _selectedDay.value = selectedDay
        _data.value = weatherOfTheWeek[selectedDay]
    }

    suspend fun requestUrl(inpputString: String) {
            lang.value?.let {
                ContentProvider.request(inpputString, it)?.let { it1 -> loadFrom(it1) }
            }
    }

    fun loadFrom(rawJson: String) {
        //Log.d("AppDebug", "loading")

        val root = WeatherGson1(rawJson).root

        if (_data.value == null) {
            for (i in 0..2) {
                weatherOfTheWeek[i] = WeatherModel()
            }
            _data.value = weatherOfTheWeek[0]
        }

        _data.value?.cityName = root.location.name
        _data.value?.date =
            DateConverter.fromEpochTime(root.current.last_updated_epoch, _lang.value!!)
        _data.value?.isDay = root.current.is_day == 1
        _data.value?.condition = root.current.condition.text

        if (_data.value?.isDay == true) {
            //_data.value?.image = dayIconsMapping?.get(root.current.condition.code)!!
            _data.value?.image = WeatherIcons.getDayMap()?.get(root.current.condition.code)!!
        } else {
            //_data.value?.image = nightIconsMapping?.get(root.current.condition.code)!!
            _data.value?.image = WeatherIcons.getNightMap()?.get(root.current.condition.code)!!
        }

        _data.value?.tempCelsius = root.current.temp_c
        _data.value?.tempCelsiusFeel = root.current.feelslike_c
        _data.value?.tempFahrenheit = root.current.temp_f
        _data.value?.tempFahrenheitFeel = root.current.feelslike_f
        _data.value?.windKph = root.current.wind_kph
        _data.value?.windMph = root.current.wind_mph
        _data.value?.humidity = root.current.humidity

        // store temperatures in an array for chart
        if (_data.value?.array_tempCelsius?.isEmpty() == true)
            root.forecast.forecastday[_selectedDay.value!!].hour.forEachIndexed { index, hour ->
                run {
                    _data.value?.array_tempCelsius?.add(ValueDataEntry(index, hour.temp_c))
                }
            }

        if (_data.value?.array_tempFahrenheit?.isEmpty() == true)
            root.forecast.forecastday[_selectedDay.value!!].hour.forEachIndexed { index, hour ->
                run {
                    _data.value?.array_tempFahrenheit?.add(ValueDataEntry(index, hour.temp_f))
                }
            }

        for (i in 1..2) {
            val day: WeatherModel? = weatherOfTheWeek[i]
            day?.cityName = _data.value?.cityName.toString()
            day?.isDay = true
            day?.condition = root.forecast.forecastday[i].day.condition.text
            //day?.image = dayIconsMapping?.get(root.forecast.forecastday[i].day.condition.code)!!
            day?.image = WeatherIcons.getDayMap()?.get(root.forecast.forecastday[i].day.condition.code)!!
            day?.tempCelsius = root.forecast.forecastday[i].day.avgtemp_c.toString()
            day?.tempFahrenheit = root.forecast.forecastday[i].day.avgtemp_f.toString()
            day?.windKph = root.forecast.forecastday[i].day.maxwind_kph.toString()
            day?.windMph = root.forecast.forecastday[i].day.maxwind_mph.toString()

            if (day?.array_tempCelsius?.isEmpty() == true)
                root.forecast.forecastday[i].hour.forEachIndexed { index, hour ->
                    run {
                        day?.array_tempCelsius?.add(ValueDataEntry(index, hour.temp_c))
                    }
                }

            if (day?.array_tempFahrenheit?.isEmpty() == true)
                root.forecast.forecastday[i].hour.forEachIndexed { index, hour ->
                    run {
                        day?.array_tempFahrenheit?.add(ValueDataEntry(index, hour.temp_f))
                    }
                }
        }
    }

    fun setLanguage(localeLang: String) {
        _lang.value = localeLang
    }

}