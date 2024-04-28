package com.example.kuteweather.model

import android.util.Log
import com.example.kuteweather.Constants
import com.example.kuteweather.api.weather.JsonProvider
import com.google.gson.Gson

class WeatherGson1 constructor(jsonString: String)  {

    val root : Root

    init {
        root = Gson().fromJson(jsonString, Root::class.java)
    }

    data class Root(val location: Location, val current: Current, val forecast: Forecast) {
    }

    data class Location(
        val name: String, val region: String, val country: String,
        val lat: Double, val lon: Double, val tz_id: String, val localtime_epoch: Long,
        val localtime: String
    ) {
    }

    data class Current(val last_updated_epoch: Long, val is_day: Int, val condition: Condition,
                       val temp_c: String, val temp_f: String,
                       val feelslike_c: String, val feelslike_f: String,
                       val wind_mph: String, val wind_kph: String,
                       val humidity: String
                       ) {
    }

    data class Forecast(val forecastday : Array<ForecastDay>) {
    }

    data class ForecastDay(val date: String, val date_epoch: Long, val day : Day,
                           val hour: Array<WeatherByHour>)

    data class Day(val maxtemp_c : Double, val maxtemp_f: Double,
                   val mintemp_c: Double, val mintemp_f: Double,
                   val avgtemp_c: Double, val avgtemp_f: Double,
                   val maxwind_kph: Double, val maxwind_mph: Double,
                   val avghumidity: Int,
                   val daily_will_it_rain: Int, val daily_will_it_snow: Int,
                   val condition: Condition,
    )

    data class WeatherByHour(
        val time_epoch : Long,
        val time: String,
        val temp_c: Double,
        val temp_f: Double,
        val condition: Condition
    )

    data class Condition(val text: String, val code: Int)

    companion object {
        fun test() {
            val gson = Gson()
            val obj = gson.fromJson(JsonProvider.json1, Root::class.java)
            for(day in obj.forecast.forecastday) {
                for (hour in day.hour) {
                    Log.d(Constants.DEBUG_LOG, hour.toString())
                }
            }
        }
    }

}