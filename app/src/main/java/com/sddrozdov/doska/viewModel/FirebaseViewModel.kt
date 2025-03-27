package com.sddrozdov.doska.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.models.DbManager

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ads>>()

    fun loadAllAds(lastTime: String) {
        dbManager.getAllAds(lastTime, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ads>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyFavoriteAds() {
        dbManager.getMyFavoriteAds(object : DbManager.ReadDataCallback {
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

    fun deleteItem(ads: Ads) {
        dbManager.deleteAd(ads, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ads)
                liveAdsData.postValue(updatedList)
            }

        })
    }

    fun onFavoriteCLick(ads: Ads) {
        dbManager.onFavoriteClick(ads, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val position = updatedList?.indexOf(ads)
                if (position != INCORRECT_POSITION_ADS) {
                    position?.let {
                        val favoriteCounter =
                            if (ads.isFavorite) ads.favoriteCounter.toInt() - REMOVE_ONE_TO_THE_FAV_COUNTER else ads.favoriteCounter.toInt() + ADD_ONE_TO_THE_FAV_COUNTER
                        updatedList[position] =
                            updatedList[position].copy(
                                isFavorite = !ads.isFavorite,
                                favoriteCounter = favoriteCounter.toString()
                            )
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ads: Ads) {
        dbManager.adViewed(ads)
    }

    companion object {
        const val INCORRECT_POSITION_ADS = -1
        const val ADD_ONE_TO_THE_FAV_COUNTER = 1
        const val REMOVE_ONE_TO_THE_FAV_COUNTER = 1

    }
}