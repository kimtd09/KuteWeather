package com.example.kuteweather.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.kuteweather.ui.favorites.FavoritesModel

@Dao
interface WeatherDao {
    @Query("SELECT * FROM favorites")
    fun getAll() : List<FavoritesModel>

    @Query("SELECT * FROM favorites")
    fun getLiveData() : LiveData<List<FavoritesModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(weatherData : FavoritesModel)

    @Delete
    fun deleteCity(favoritesModel: FavoritesModel)

    @Query("SELECT city FROM favorites WHERE city=:city")
    fun getCity(city: String) : String
}