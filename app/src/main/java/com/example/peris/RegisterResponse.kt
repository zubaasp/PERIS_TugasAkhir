package com.example.peris

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import com.google.gson.annotations.SerializedName

data class RegisterResponse (
    @SerializedName("message")
    val message: String,
    @SerializedName("token")
    val token: String
)

