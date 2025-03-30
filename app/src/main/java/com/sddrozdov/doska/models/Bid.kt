package com.sddrozdov.doska.models

import java.io.Serializable

data class Bid(
    val userId: String,
    val amount: String,
    val timestamp: Long
) : Serializable