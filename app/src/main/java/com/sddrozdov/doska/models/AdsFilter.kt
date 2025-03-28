package com.sddrozdov.doska.models

data class AdsFilter(
    val time: String? = null,
    val cat_time: String? = null,

    val category_country_time: String? = null,
    val category_country_city_time: String? = null,
    val category_country_city_index_time: String? = null,
    val category_index_time: String? = null,

    val country_time: String? = null,
    val country_city_time: String? = null,
    val country_city_index_time: String? = null,
    val index_time: String? = null,

)
