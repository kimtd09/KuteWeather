package com.example.kuteweather.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kuteweather.ui.favorites.FavoritesModel
import com.google.android.gms.maps.model.LatLng

class MapsViewModel : ViewModel() {

    var coordinates = MutableLiveData(LatLng(0.0,0.0))

    fun getString() : String {
        return "${coordinates.value?.latitude.toString()},${coordinates.value?.longitude.toString()}"
    }

    fun getParcelable() : FavoritesModel {
        // TODO request API to find city and country
        return FavoritesModel(getString(),"n/a", coordinates.value?.latitude!!, coordinates.value?.longitude!!)
    }

}