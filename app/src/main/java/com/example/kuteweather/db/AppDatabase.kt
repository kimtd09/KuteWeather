package com.example.kuteweather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kuteweather.ui.favorites.FavoritesModel

@Database(entities = [FavoritesModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "database.db"

        @Volatile
        private var instance : AppDatabase? = null

        private val LOCK = Object()

        fun getInstance(context: Context) : AppDatabase? {
            if(instance == null) {
                synchronized(LOCK) {
                    if(instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            DB_NAME
                        ).build()
                    }
                }
            }

            return instance
        }
    }

    abstract fun dao() : WeatherDao
}