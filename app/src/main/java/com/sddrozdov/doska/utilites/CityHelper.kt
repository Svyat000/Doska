package com.sddrozdov.doska.utilites

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

object CityHelper {
    fun getAllCountries(context: Context): ArrayList<String> {
        val tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesAndTheirCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFileCountriesAndCities: String = String(bytesArray)
            val jsonObject = JSONObject(jsonFileCountriesAndCities)
            val countriesNames = jsonObject.names()
            if (countriesNames != null) {
                for (i in 0 until countriesNames.length()) {
                    tempArray.add(countriesNames.getString(i))
                }
            }

        } catch (e: IOException) {

        }
        return tempArray
    }
}