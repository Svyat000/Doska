package com.sddrozdov.doska.models

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager {
    val db = Firebase.database.getReference(MAIN)
    val auth = Firebase.auth

    fun publicationAd(ads: Ads, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) {
            db.child(ads.key ?: "Empty").child(auth.uid!!).child(AD).setValue(ads)
                .addOnCompleteListener {
                    //if(it.isSuccessful)
                    finishWorkListener.onFinish()
                }
        }
    }

    fun adViewed(ads: Ads) {
        var counter = ads.viewsCounter.toInt()
        counter++
        if (auth.uid != null) {
            db.child(ads.key ?: "Empty").child(INFO_AD)
                .setValue(InfoItem(counter.toString(), ads.emailsCounter, ads.callsCounter))
        }
    }

    private fun addFavoriteAds(ads: Ads, finishWorkListener: FinishWorkListener) {
        ads.key?.let {
            auth.uid?.let { it1 ->
                db.child(it).child(FAFORITE_ADS).child(it1).setValue(it1).addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }

    private fun deleteFavoriteAds(ads: Ads, finishWorkListener: FinishWorkListener) {
        ads.key?.let {
            auth.uid?.let { it1 ->
                db.child(it).child(FAFORITE_ADS).child(it1).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }

    fun onFavoriteClick(ads: Ads, finishWorkListener: FinishWorkListener) {
        if (ads.isFavorite) deleteFavoriteAds(ads, finishWorkListener)
        else addFavoriteAds(ads, finishWorkListener)
    }


    fun getMyAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/AD/uid").equalTo(auth.uid)
        readDataFromDB(query, readDataCallback)
    }

    fun getAllAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild(auth.uid + "/AD/price")
        readDataFromDB(query, readDataCallback)
    }

    fun deleteAd(ads: Ads, finishWorkListener: FinishWorkListener) {
        if (ads.key == null || ads.uid == null) return
        db.child(ads.key).child(ads.uid).removeValue().addOnCompleteListener {
            finishWorkListener.onFinish()
        }
    }

    private fun readDataFromDB(query: Query, readDataCallback: ReadDataCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {

            val adArray = ArrayList<Ads>()

            override fun onDataChange(snapshot: DataSnapshot) {

                for (i in snapshot.children) {

                    var ad: Ads? = null

                    i.children.forEach {
                        if (ad == null) ad = it.child(AD).getValue(Ads::class.java)
                    }
                    val infoItem = i.child(INFO_AD).getValue(InfoItem::class.java)

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    if (ad != null) adArray.add(ad!!)

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
        fun readData(list: ArrayList<Ads>) {
            Log.d("MyTag", " CALLBACK LOG IN INTERFACE")
        }
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        const val AD = "AD"
        const val MAIN = "main"
        const val INFO_AD = "info"
        const val FAFORITE_ADS = "favorite"
    }
}
