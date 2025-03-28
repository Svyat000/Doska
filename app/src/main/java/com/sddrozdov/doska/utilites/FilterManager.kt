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

    fun getFilter(filter: String): String {
        val stringBuilderNode = StringBuilder()
        val stringBuilderFilter = StringBuilder()
        val tempArray = filter.split("_")
        if (tempArray[0] != "EMPTY") {
            stringBuilderNode.append("country_")
            stringBuilderFilter.append("${tempArray[0]}_")
        }
        if (tempArray[1] != "EMPTY") {
            stringBuilderNode.append("city_")
            stringBuilderFilter.append("${tempArray[1]}_")
        }
        if (tempArray[2] != "EMPTY") {
            stringBuilderNode.append("index_")
            stringBuilderFilter.append(tempArray[2])
        }
        stringBuilderNode.append("time")
        return "$stringBuilderNode|$stringBuilderFilter"

    }
}