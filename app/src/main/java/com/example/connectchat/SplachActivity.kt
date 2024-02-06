package com.example.connectchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.connectchat.chatscreen.Chatscreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplachActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)



        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,Chatscreen::class.java)
            startActivity(intent)
        }, 2000)

    }
}