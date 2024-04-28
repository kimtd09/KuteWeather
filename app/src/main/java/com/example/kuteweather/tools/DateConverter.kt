package com.example.kuteweather.tools

import android.util.Log
import com.example.kuteweather.Constants
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {
    companion object {
        fun fromEpochTime(time : Long, lang: String) : String {
            Log.d(Constants.DEBUG_LOG, "epoc time=$time")
            val date = Date(time*1000) // Date takes time in milliseconds

            if(lang == "fr") {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                return sdf.format(date)
            }
            else {
                val sdf = SimpleDateFormat("hh:mm a")
                return sdf.format(date)
            }
        }
    }
}