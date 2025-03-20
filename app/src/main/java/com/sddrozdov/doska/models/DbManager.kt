package com.sddrozdov.doska.models

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publicationAd(ads: Ads) {
        if (auth.uid != null) {
            db.child(ads.key ?: "Empty").child(auth.uid!!).child("AD").setValue(ads)
        }
    }

    fun readDataFromDB(readDataCallback: ReadDataCallback?) {
        db.addListenerForSingleValueEvent(object : ValueEventListener {

            val adArray = ArrayList<Ads>()

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val ad = i.children.iterator().next().child("AD").getValue(Ads::class.java)
                    if (ad != null) {
                        adArray.add(ad)
                    }
                }
                Log.d("MyTag", " CALLBACK LOG IN DBMANAGER")
                readDataCallback?.readData(adArray)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")

            }
        })
    }
    interface ReadDataCallback {
        fun readData(list: ArrayList<Ads>){
            Log.d("MyTag"," CALLBACK LOG IN INTERFACE")
        }
    }
}
