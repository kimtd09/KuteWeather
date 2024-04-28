package com.example.kuteweather.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.kuteweather.db.AppRepository

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    // private
    private val _selectedIndex = MutableLiveData(-1)

    // public
    val liveData = AppRepository.getInstance(application.applicationContext)?.observable
    val selectedIndex = _selectedIndex
    val listOfTemperatures = ArrayList<MutableLiveData<String>>()
    val listOfIcons = ArrayList<MutableLiveData<Integer>>()
    val listOfProgressBarStatus = ArrayList<MutableLiveData<Boolean>>()
    lateinit var temp : String
}