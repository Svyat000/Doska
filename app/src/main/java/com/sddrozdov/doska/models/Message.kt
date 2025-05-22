package com.sddrozdov.doska.models

data class Message(
    val text: String = "",
    val senderId: String = "",
    val senderName: String? = null,
    val timestamp: Long = 0
)
