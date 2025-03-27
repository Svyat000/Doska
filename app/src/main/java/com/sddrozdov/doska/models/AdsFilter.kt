package com.sddrozdov.doska.models

data class AdsFilter(
    val time: String? = null,
    val catTime: String? = null,

    val categoryCountryTime: String? = null,
    val categoryCountryCityTime: String? = null,
    val categoryCountryCityIndexTime: String? = null,
    val categoryIndexTime: String? = null,

    val countryTime: String? = null,
    val countryCityTime: String? = null,
    val countryCityIndexTime: String? = null,
    val indexTime: String? = null,

)
