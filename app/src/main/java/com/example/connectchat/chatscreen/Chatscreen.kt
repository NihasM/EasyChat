package com.example.connectchat.chatscreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectchat.R
import com.example.connectchat.WebSocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Random
import java.util.UUID

class Chatscreen : AppCompatActivity() {
    companion object {
        lateinit var sharedPref: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
    }

    private val webSocketUrl = "wss://beryl-time-boater.glitch.me"
    private lateinit var webSocketClient: WebSocketClient
    private val chatList: ArrayList<ChatModel> = ArrayList()
    private var edtmsg: EditText? = null
    private var toolbar: ImageView? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var chatAdapter: ChatAdapter

    private val drawableList = listOf(
        R.drawable.backimg1,
        R.drawable.backimg2,
        R.drawable.backimg3,
        R.drawable.backimg4,
        R.drawable.backimg5
    )
    private var currentDrawableIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatscreen)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            val view: View = window.decorView
            view.systemUiVisibility = view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.your_status_bar_color, theme)
        }*/

        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerview)
        edtmsg = findViewById(R.id.edtmsg)
        toolbar = findViewById(R.id.toolbarIcon)
        mainLayout = findViewById(R.id.mainlayout)
        val imageView: ImageView = findViewById(R.id.sendimg)

        // Initialize RecyclerView and Adapter
        chatAdapter = ChatAdapter(chatList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Chatscreen).apply {
                stackFromEnd = true
                reverseLayout = false
            }
        }
        recyclerView.adapter = chatAdapter

        // Initialize WebSocket connection
        webSocketClient = WebSocketClient(webSocketUrl)
        webSocketClient.connect()

        if(sharedPref.getString("bgnum","0")=="1"){
            mainLayout.setBackgroundResource(R.drawable.backimg2)
        }else if(sharedPref.getString("bgnum","0")=="2"){
            mainLayout.setBackgroundResource(R.drawable.backimg3)
        }else if(sharedPref.getString("bgnum","0")=="3"){
            mainLayout.setBackgroundResource(R.drawable.backimg4)
        }else if(sharedPref.getString("bgnum","0")=="4"){
            mainLayout.setBackgroundResource(R.drawable.backimg5)
        }else {
            mainLayout.setBackgroundResource(R.drawable.backimg1)
        }

        toolbar?.setOnClickListener {
            showPopupMenu(it)
        }

        imageView.setOnClickListener {
            val sdf = SimpleDateFormat("HH:mm")
            val currentDate = sdf.format(Date())
            webSocketClient.send(edtmsg?.text.toString().trim()+","+currentDate+","+sharedPref.getString("name","Player")+","+sharedPref.getString("uuid","00000"))
            Log.d("kool", "onCreate: "+edtmsg?.text.toString().trim()+",13:13,"+sharedPref.getString("name","Player")+sharedPref.getString("uuid","00000"))
            edtmsg?.setText("")
        }

        Log.d("kool", "onCreate: "+sharedPref.getString("name","Player"))
        if(sharedPref.getString("name","Player")=="Player"){
            showCustomDialog()

        }




        webSocketClient.setMessageListener { message ->
            // Handle the incoming message
            Log.d("kool", "onCreate: $message")
            //val extractedText = extractTextInBrackets(message)
            val extractedText = message
            val msg = extractedText?.trim()?.split(",")?.get(0)
            val time = extractedText?.trim()?.split(",")?.get(1)
            val name = extractedText?.trim()?.split(",")?.get(2)
            val uuid = extractedText?.trim()?.split(",")?.get(3)
            Log.d("kool", "Received message1: $extractedText/$msg/$time/$name/$time")
            CoroutineScope(Dispatchers.Main).launch {
                // Delay for 1 second (1000 milliseconds) before adding the message to the adapter
                delay(100)
                val chatModel = ChatModel(name ?: name!!, uuid ?: uuid!!, time ?: time!!, msg ?: msg!!)
                chatList.add(chatModel)

                chatAdapter.notifyDataSetChanged()

                scrollToBottom()
            }


            Log.d("kool", "Received message: $extractedText/$msg/$time/$name/$time")
        }
    }

    /*fun extractTextInBrackets(input: String): String? {

        if(input.contains("size=")){
            val splittext = input.split("t=")[1].trim().replace("]", "")
            Log.d("kool", "extractTextInBrackets: $splittext")
            return splittext
        }else{
            val regex = "\\[text=(.*?)\\]".toRegex()
            val matchResult = regex.find(input)
            return matchResult?.groupValues?.get(1)
        }
    }*/

    fun generateRandomUuid(): String {
        val random = Random()
        val randomUuid = UUID.randomUUID()
        // Get the least significant bits (64 bits)
        val leastSignificantBits = randomUuid.leastSignificantBits
        // Convert the least significant bits to an unsigned 64-bit long
        val unsignedLeastSignificantBits = leastSignificantBits and Long.MAX_VALUE
        // Take the last 5 digits
        val last5Digits = unsignedLeastSignificantBits % 100000
        // Format the result to have leading zeros if needed
        return String.format("%05d", last5Digits)
    }

    private fun showCustomDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog_layout, null)

        // Set a transparent background for the dialog
        builder.setView(dialogView)
        val dialog = builder.create()
        // Set the background color to be transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        // Optionally set other properties of the dialog, such as animations, etc.
        val edtname: EditText = dialogView.findViewById(R.id.edtname)
        val txterror: TextView = dialogView.findViewById(R.id.txterror)
        val btnconnect: Button = dialogView.findViewById(R.id.btnconnect)
        val processbar: ProgressBar = dialogView.findViewById(R.id.processbar)
        val llprocess: LinearLayout = dialogView.findViewById(R.id.process)

        btnconnect.setOnClickListener {
            if(edtname.text.toString().trim()==""||edtname.text.toString().trim()==null){
                txterror.visibility=View.VISIBLE
            }else {

                editor.apply {
                    putString("name", edtname.text.toString().trim())
                    putString("uuid", generateRandomUuid())
                    apply()
                }
                txterror.visibility=View.GONE
                btnconnect.visibility=View.GONE
                llprocess.visibility=View.VISIBLE

                CoroutineScope(Dispatchers.Main).launch {
                    // Delay for 1 second (1000 milliseconds) before adding the message to the adapter
                    delay(5000)
                    dialog.dismiss()
                }
            }


        }
        dialog.show()
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popupmenu, popupMenu.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_1 -> {
                    // Increment the index and loop back to 0 if it reaches the end
                    currentDrawableIndex = (currentDrawableIndex + 1) % drawableList.size

                    // Set the new drawable to the ImageView
                    mainLayout.setBackgroundResource(drawableList[currentDrawableIndex])
                    editor.apply {
                        putString("bgnum", currentDrawableIndex.toString())
                        apply()
                    }
                    true
                }
                // Add more cases if you have additional menu items

                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }

    private fun scrollToBottom() {
        recyclerView.scrollToPosition(chatList.size - 1)
    }


}
