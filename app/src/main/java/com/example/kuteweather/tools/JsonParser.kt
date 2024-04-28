package com.example.kuteweather.tools

import org.json.JSONObject

class Response(json: String) : JSONObject(json) {
    val type: String? = this.optString("type")
    val data = this.optJSONArray("data")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
        ?.map { JsonData(it.toString()) } // transforms each JSONObject of the array into Foo
}

class Response2(json: String) : JSONObject(json) {
    val type: String? = this.optString("type")
    val data = this.optJSONArray("results")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
}

class GoogleGeocodingParserL1(json: String) : JSONObject(json) {
    val status = this.optString("status")
    val results = this.optJSONArray("results")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
        ?.map{ GoogleGeocodingParserL2(it.toString()) }
}

class GoogleGeocodingParserL2(json: String) : JSONObject(json) {
    val addressComponents = this.optJSONArray("address_components")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
}

class GoogleGeocodingParserL3(json: String) : JSONObject(json) {
    val component = this.optJSONArray("types")
        ?.let { 0.until(it.length()).map { i -> it.optJSONObject(i) } } // returns an array of JSONObject
}

class JsonData(json: String) : JSONObject(json) {
    val name: String? = this.optString("name")
    val region: String? = this.optString("region")
    val country: String? = this.optString("country")
    val temp: String? = this.optString("temp_c")

    override fun toString(): String {
        return super.toString()
        return "\ncity name=$name\nregion=$region\ncountry=$country\ntemperature=$temp"
    }
}

