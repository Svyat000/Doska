package com.sddrozdov.doska.models

import java.io.Serializable

data class Ads(
    val country: String? = null,
    val city: String? = null,
    val email: String? = null,
    val tel: String? = null,
    val index: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val key: String? = null,
    val uid: String? = null,


    var favoriteCounter: String = "0",

    val mainImage: String = "empty",
    val image2: String = "empty",
    val image3: String = "empty",
    var isFavorite: Boolean = false,
    val time: String = "0",


    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0",

    ) : Serializable
