package com.sddrozdov.doska.database

import android.util.Log
import com.sddrozdov.doska.models.Ads

interface ReadDataCallback {
    fun readData(list: List<Ads>){
        Log.d("MyTag"," CALLBACK LOG IN INTERFACE")
    }
}