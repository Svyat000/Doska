package com.sddrozdov.doska.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.reference

    fun publicateAd(){
        db.setValue("Test Again")
    }
}