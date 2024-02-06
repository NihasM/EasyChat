package com.example.connectchat

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Request
import okio.ByteString

class WebSocketClient(private val url: String) {
    private var webSocket: WebSocket? = null
    private var messageListener: ((String) -> Unit)? = null

    fun connect() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                // Handle the WebSocket connection being opened
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // Handle the incoming message
                messageListener?.invoke(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                // Handle the incoming message in bytes
                messageListener?.invoke(bytes.toString())
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                // Handle the WebSocket being closed
                connect()
                Log.d("okok", "onClosed: ")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Handle the failure of the WebSocket connection
                connect()
                Log.d("okok", "onFailure: ")
            }

        })
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "User-initiated")
    }

    // Set a listener for incoming messages
    fun setMessageListener(listener: (String) -> Unit) {
        messageListener = listener
    }


}
