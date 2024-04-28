package com.example.kuteweather.ui.favorites

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorites")
data class FavoritesModel(
    @PrimaryKey
    val city: String,
    var country: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable