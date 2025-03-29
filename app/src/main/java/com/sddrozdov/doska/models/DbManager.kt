package com.sddrozdov.doska.models

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.utilites.FilterManager

class DbManager {
    val db = Firebase.database.getReference(MAIN)
    val auth = Firebase.auth
    //val dbStorage = Firebase.storage.getReference(MAIN)

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
                    val favoriteCounter = i.child(FAVORITE_ADS).childrenCount
                    val isFavorite = auth.uid?.let {
                        i.child(FAVORITE_ADS).child(it).getValue(String::class.java)
                    }
                    ad?.isFavorite = isFavorite != null
                    ad?.favoriteCounter = favoriteCounter.toString()

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

    private fun readNextPageDataFromDB(query: Query, filter: String, orderBy: String, readDataCallback: ReadDataCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            val adArray = ArrayList<Ads>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    var ad: Ads? = null
                    i.children.forEach {
                        if (ad == null) ad = it.child(AD).getValue(Ads::class.java)
                    }
                    val infoItem = i.child(INFO_AD).getValue(InfoItem::class.java)
                    val filterValue = i.child(INFO_AD).child(orderBy).value.toString()

                    val favoriteCounter = i.child(FAVORITE_ADS).childrenCount
                    val isFavorite = auth.uid?.let {
                        i.child(FAVORITE_ADS).child(it).getValue(String::class.java)
                    }
                    ad?.isFavorite = isFavorite != null
                    ad?.favoriteCounter = favoriteCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"
                    if (ad != null && filterValue.startsWith(filter)) adArray.add(ad!!)

                }
                Log.d("MyTag", " CALLBACK LOG IN DBMANAGER")
                readDataCallback?.readData(adArray)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")

            }
        })
    }

    fun publicationAd(ads: Ads, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) {
            db.child(ads.key ?: "Empty").child(auth.uid!!).child(AD).setValue(ads)
                .addOnCompleteListener {
                    val adsFilter =
                        FilterManager.createFilter(ads)//AdsFilter(ads.time, "${ads.category}_${ads.time}")
                    db.child(ads.key ?: "Empty").child(AD_FILTER).setValue(adsFilter)
                        .addOnCompleteListener {
                            finishWorkListener.onFinish()
                        }
                }
        }
    }

    fun deleteAd(ads: Ads, finishWorkListener: FinishWorkListener) {
        if (ads.key == null || ads.uid == null) return
        db.child(ads.key).child(ads.uid).removeValue().addOnCompleteListener {
            finishWorkListener.onFinish()
        }
    }

    fun addViewsToAd(ads: Ads) {
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
                db.child(it).child(FAVORITE_ADS).child(it1).setValue(it1).addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }

    private fun deleteFavoriteAds(ads: Ads, finishWorkListener: FinishWorkListener) {
        ads.key?.let {
            auth.uid?.let { it1 ->
                db.child(it).child(FAVORITE_ADS).child(it1).removeValue().addOnCompleteListener {
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

    fun getMyFavoriteAds(readDataCallback: ReadDataCallback?) {
        val query = db.orderByChild("/favorite/${auth.uid}").equalTo(auth.uid)
        readDataFromDB(query, readDataCallback)
    }

    fun getAllAdsFirstPage(filter: String, readDataCallback: ReadDataCallback?) {
        val query = if (filter.isEmpty()) {
            db.orderByChild(AD_FILTER_TIME).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsWithFilterFirstPage(filter)
        }
        readDataFromDB(query, readDataCallback)
    }

    fun getAllAdsWithFilterFirstPage(globalFilter: String): Query {
        val orderBy = globalFilter.split("|")[0]
        val filter = globalFilter.split("|")[1]
        return db.orderByChild("/AdFilter/$orderBy").startAt(filter).endAt(filter + "\uf8ff")
            .limitToLast(ADS_LIMIT)
    }

    fun getAllAdsNextPage(time: String, filter: String, readDataCallback: ReadDataCallback?) {
        if (filter.isEmpty()) {
            val query = db.orderByChild(AD_FILTER_TIME).endBefore(time).limitToLast(ADS_LIMIT)
            readDataFromDB(query, readDataCallback)
        } else {
            getAllAdsByFilterNextPage(filter, time, readDataCallback)
        }
    }

    private fun getAllAdsByFilterNextPage(globalFilter: String, time: String, readDataCallback: ReadDataCallback?) {
        val orderBy = globalFilter.split("|")[0]
        val filter = globalFilter.split("|")[1]
        val query = db.orderByChild("/AdFilter/$orderBy").endBefore(filter + "_$time")
            .limitToLast(ADS_LIMIT)
        readNextPageDataFromDB(query, filter, orderBy, readDataCallback)
    }

    fun getAllAdsFromCategoryFirstPage(category: String, filter: String, readDataCallback: ReadDataCallback?) {
        val query = if (filter.isEmpty()) {
            db.orderByChild(AD_FILTER_CATEGORY_TIME).startAt(category).endAt(category + "_\uf8ff")
                .limitToLast(ADS_LIMIT)
        } else {
            getAllAdsFromCategoryWithFilterFirstPage(category, filter)
        }
        readDataFromDB(query, readDataCallback)
    }

    private fun getAllAdsFromCategoryWithFilterFirstPage(category: String, globalFilter: String): Query {
        val orderBy = category + "_" + globalFilter.split("|")[0]
        val filter = category + "_" + globalFilter.split("|")[1]

        return db.orderByChild("/AdFilter/$orderBy").startAt(filter).endAt(filter + "\uf8ff")
            .limitToLast(ADS_LIMIT)
    }

    fun getAllAdsFromCategoryNextPage(category: String, time: String,filter: String, readDataCallback: ReadDataCallback?) {
        if(filter.isEmpty()){
            val query =
                db.orderByChild(AD_FILTER_CATEGORY_TIME).endBefore(category + "_" +  time).limitToLast(ADS_LIMIT)
            readDataFromDB(query, readDataCallback)
        } else{
            getAllAdsFromCategoryWithFilterNextPage(category,time,filter,readDataCallback)
        }
    }

    private fun getAllAdsFromCategoryWithFilterNextPage(category: String, time: String, globalFilter: String, readDataCallback: ReadDataCallback?) {
        val orderBy = category + "_" + globalFilter.split("|")[0]
        val filter = category + "_" + globalFilter.split("|")[1]
        val query =
            db.orderByChild("AdFilter/$orderBy").endBefore(filter + "_" + time).limitToLast(ADS_LIMIT)
        readNextPageDataFromDB(query,filter,orderBy,readDataCallback)
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
        const val FAVORITE_ADS = "favorite"
        const val ADS_LIMIT = 3
        const val AD_FILTER = "AdFilter"
        const val AD_FILTER_TIME = "/AdFilter/time"
        const val AD_FILTER_CATEGORY_TIME = "/AdFilter/cat_time"
    }
}