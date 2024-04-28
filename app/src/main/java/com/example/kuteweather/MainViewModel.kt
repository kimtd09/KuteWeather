package com.example.kuteweather

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager

/**
 * This viewmodel is intended to store non critical application settings like units
 * It is accessible to all fragments
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    val app = application
    var lang : MutableLiveData<String> = MutableLiveData("")
    var isCelsius : MutableLiveData<Boolean> = MutableLiveData(true)
    var isKph : MutableLiveData<Boolean> = MutableLiveData(false)
    var latitude = 0.0
    var longitude = 0.0
    var isGeolocalized = false
    lateinit var context : Context

    override fun toString(): String {
        return "MainViewModel(lang='${lang.value}', temp='${isCelsius.value}', wind='${isKph.value}')"
    }

    fun toggleTempUnit() {
        isCelsius.value = !isCelsius.value!!
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val editor = sharedPreferences.edit()
        if(isCelsius.value == true) {
            editor.putString(app.getString(R.string.key_preference_temperature), app.getString(R.string.celsius_unit))
        }
        else {
            editor.putString(app.getString(R.string.key_preference_temperature), app.getString(R.string.fahrenheit_unit))
        }
        editor.commit()
    }

    fun toggleWindUnit() {
        isKph.value = !isKph.value!!
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
        val editor = sharedPreferences.edit()
        if(isKph.value == true) {
            editor.putString(app.getString(R.string.key_preference_wind), app.getString(R.string.wind_kph))
        }
        else {
            editor.putString(app.getString(R.string.key_preference_wind), app.getString(R.string.wind_mph))
        }
        editor.commit()
    }

}
