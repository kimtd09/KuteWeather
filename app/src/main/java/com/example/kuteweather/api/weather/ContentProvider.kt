package com.example.kuteweather.api.weather

import android.util.Log
import com.example.kuteweather.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset
import kotlin.jvm.Throws

class ContentProvider {

    /*
        Example
        http://api.weatherapi.com/v1/current.json?key=9935050ed56e4949931124755222104&q=London&aqi=no
     */

    companion object {
        private val URL =
            "https://api.weatherapi.com/v1/forecast.json?key=9935050ed56e4949931124755222104&days=5&q="

        suspend fun request(cityName: String): String?  {
            return request(cityName, "en")
        }

        suspend fun request(cityName: String, lang: String): String?  {
            var result: String? = null

            // for low API compatibility
            val nameWithoutSpace = cityName.replace(" ", "%20")

            withContext(Dispatchers.IO) {
                Log.d(Constants.DEBUG_LOG, "URL requested: $URL$nameWithoutSpace&lang=$lang")
                val url = URL("$URL$nameWithoutSpace&lang=$lang")
               try {
                    result = url.readText(Charset.defaultCharset())
                   //Log.d("AppDebug" , "result=$result")
               }catch(e: Exception) {
                    Log.d(Constants.DEBUG_LOG, "Exception ${e.message}")
                    Log.d(Constants.DEBUG_LOG, "request failed for $cityName")
                }
            }

            return result
        }

        suspend fun request(latitude: Double, longitude: Double, lang: String): String? {
            return request("$latitude,$longitude",lang)
        }
    }
}