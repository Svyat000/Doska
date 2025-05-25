package com.sddrozdov.doska.models

data class Dialog(
    val id: String = "",
    val adId: String = "",
    val name: String = "",
    val participants: Map<String?, Boolean> = emptyMap(),
    val lastMessage: String = "",
    val timestamp: Long = 0,
    val userImageUrl: String = ""
)