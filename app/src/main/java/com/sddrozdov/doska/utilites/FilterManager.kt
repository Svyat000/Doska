package com.sddrozdov.doska.utilites


import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.models.AdsFilter

object FilterManager {

    fun createFilter(ads: Ads): AdsFilter {
        return AdsFilter(
            ads.time,
            "${ads.category}_${ads.time}",
            "${ads.category}_${ads.country}_${ads.time}",
            "${ads.category}_${ads.country}_${ads.city}_${ads.time}",
            "${ads.category}_${ads.country}_${ads.city}_${ads.index}_${ads.time}",
            "${ads.category}_${ads.index}_${ads.time}",
            "${ads.country}_${ads.time}",
            "${ads.country}_${ads.city}_${ads.time}",
            "${ads.country}_${ads.city}_${ads.index}_${ads.time}",
            "${ads.index}_${ads.time}"

        )
    }
}