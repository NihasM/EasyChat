package com.example.connectchat

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PuchNotificationService: FirebaseMessagingService()   {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("kool", "onNewToken: $token")
    }
}