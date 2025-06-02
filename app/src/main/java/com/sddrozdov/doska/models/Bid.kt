package com.sddrozdov.doska.models

import java.io.Serializable

data class Bid(
    val amount: String = "",
    val timestamp: Long = 0,
    val userId: String = ""
) : Serializable