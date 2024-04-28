package com.example.kuteweather.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kuteweather.Constants
import com.example.kuteweather.db.AppRepository
import com.google.android.gms.maps.model.LatLng

class SearchViewModel : ViewModel() {

    private val _isValid = MutableLiveData(false)
    private val _city = MutableLiveData("")
    private val _country = MutableLiveData("")
    private val _countryShortName = MutableLiveData("")
    private val _latlng = MutableLiveData(LatLng(0.0,0.0))
    private val _isFavorite = MutableLiveData(false)

    val isValid : LiveData<Boolean> = _isValid
    var city = _city
    var country = _country
    var countryShortName = _countryShortName
    var latLng = _latlng
    var isFavorite = _isFavorite

    fun checkText(input: String) {
        // TODO regex
        //Log.d(Constants.DEBUG_LOG, input)
        val namePattern = Regex("^[a-zA-Z]*$")

        // https://stackoverflow.com/a/18690202/18629381
        val gpsPattern = Regex("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)\$")
        _isValid.value = input.matches(namePattern) || input.matches(gpsPattern)
    }

    fun resetData() {
        _city.value = ""
        _country.value = ""
        _countryShortName.value = ""
        _latlng.value = LatLng(0.0,0.0)
        _isFavorite.value = false
    }
}