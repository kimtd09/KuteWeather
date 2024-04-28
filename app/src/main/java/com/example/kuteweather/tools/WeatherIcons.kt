package com.example.kuteweather.tools

class WeatherIcons {
    companion object {
        private var dayMap : HashMap<Int, Int>? = null
        private var nightMap : HashMap<Int, Int>? = null

        fun getDayMap() : HashMap<Int, Int>? {
            if(dayMap == null) {
                dayMap = HashMap()
            }
            return dayMap
        }

        fun getNightMap() : HashMap<Int, Int>? {
            if(nightMap == null) {
                nightMap = HashMap()
            }
            return nightMap
        }
    }
}