package com.example.kuteweather.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kuteweather.ui.favorites.FavoritesModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository private constructor(context: Context) {

    companion object {
        private var instance: AppRepository? = null

        fun getInstance(context: Context): AppRepository? {
            if (instance == null) {
                instance = AppRepository(context)
            }

            return instance
        }
    }

    var database: AppDatabase? = AppDatabase.getInstance(context)
    var observable : LiveData<List<FavoritesModel>>? = database?.dao()?.getLiveData()

    fun loadData() {
        CoroutineScope(Dispatchers.Default).launch {
            //liveData?.postValue(database?.dao()?.getAll())
        }
    }

    fun loadData2() {
        CoroutineScope(Dispatchers.Default).launch {
            //liveData = database?.dao()?.getLiveData()
        }
    }


    fun addData(data: FavoritesModel) {
        CoroutineScope(Dispatchers.Default).launch {
            database?.dao()?.addCity(data)
        }
    }

    fun deleteCity(city: FavoritesModel) {
        CoroutineScope(Dispatchers.Default).launch {
            database?.dao()?.deleteCity(city)
        }
    }

    suspend fun getCity(city: String): String? {
        var result: String? = null
        CoroutineScope(Dispatchers.Default).launch {
            result = database?.dao()?.getCity(city)
        }.join()
        return result
    }

//    init {
//        observableData = database?.dao()?.getLiveData()!!
//    }
//
//    fun addAllPersons(list: List<PersonEntity>) {
//        executor.execute { database?.dao()?.insertAll(list) }
//    }
//
//    fun deleteAll() {
//        executor.execute { database?.dao()?.deleteAll() }
//    }
//
//    fun deletePerson(personEntity: PersonEntity) {
//        executor.execute{ database?.dao()?.deletePerson(personEntity) }
//    }
//
//    fun deletePersons(list: List<PersonEntity>) {
//        executor.execute{ database?.dao()?.deletePersons(list) }
//    }
}