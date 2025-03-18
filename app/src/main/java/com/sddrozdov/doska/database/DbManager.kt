package com.sddrozdov.doska.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.models.Ads

class DbManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publicationAd(ads: Ads) {
        if(auth.uid != null){
            db.child(ads.key ?: "Empty").child(auth.uid!!).child("AD").setValue(ads)
        }
    }
}