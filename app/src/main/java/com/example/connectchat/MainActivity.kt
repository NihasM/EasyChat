package com.example.connectchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.connectchat.chatscreen.Chatscreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val webSocketUrl = "ws://beryl-time-boater.glitch.me"

    private lateinit var webSocketClient: WebSocketClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webSocketClient = WebSocketClient(webSocketUrl)


        val btnStart: Button = findViewById(R.id.btnstart)
        val btncheck: Button = findViewById(R.id.btncheck)
        btnStart.setOnClickListener {
            webSocketClient.connect()
        }

        btncheck.setOnClickListener {
            val intent = Intent(this, Chatscreen::class.java)

            startActivity(intent)

        }



        webSocketClient.setMessageListener { message ->
            // Handle the incoming message
            Log.d("kool", "Received message: $message")
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }

}

//testcommit