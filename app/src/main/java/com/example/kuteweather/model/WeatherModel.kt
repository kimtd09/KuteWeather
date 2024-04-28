package com.example.kuteweather.model

import com.anychart.chart.common.dataentry.DataEntry
import com.example.kuteweather.R
import java.lang.AssertionError

class WeatherModel {

    companion object {
        val noneString = "-"
    }

    var isDay: Boolean = false
    var cityName = noneString
    var date = noneString
    var condition = noneString
    var image = R.drawable.day_113
    var tempCelsius = noneString
    var tempFahrenheit = noneString
    var tempCelsiusFeel = noneString
    var tempFahrenheitFeel = noneString
    var windKph = noneString
    var windMph = noneString
    var humidity = noneString
    private var _array_tempCelsius: ArrayList<DataEntry>? = null
    private var _array_tempFahrenheit: ArrayList<DataEntry>? = null

    val array_tempCelsius: ArrayList<DataEntry>
        get() {
            if (_array_tempCelsius == null) {
                _array_tempCelsius = ArrayList()
            }

            return _array_tempCelsius ?: throw AssertionError("array null")
        }

    val array_tempFahrenheit: ArrayList<DataEntry>
        get() {
            if (_array_tempFahrenheit == null) {
                _array_tempFahrenheit = ArrayList()
            }

            return _array_tempFahrenheit ?: throw AssertionError("array null")
        }
}