package com.sddrozdov.doska.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.models.DbManager

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ads>>()

    fun loadAllAds() {
        dbManager.getAllAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ads>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyAds() {
        dbManager.getMyAds(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ads>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteItem(ad: Ads){
        dbManager.deleteAd(ad, object : DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }

        })
    }

    fun adViewed(ads: Ads){
        dbManager.adViewed(ads)
    }


}