package com.example.connectchat.chatscreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.connectchat.R

class ChatAdapter(private val chatList: List<ChatModel>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msgTextView: TextView = itemView.findViewById(R.id.txtmsg)
        val txtmymsg: TextView = itemView.findViewById(R.id.txtmymsg)
        val txttimename: TextView = itemView.findViewById(R.id.txttimename)
        val txtmytimename: TextView = itemView.findViewById(R.id.txtmytimename)
        val llincoming: LinearLayout = itemView.findViewById(R.id.llincoming)
        val lloutgoing: LinearLayout = itemView.findViewById(R.id.lloutgoing)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]

        holder.msgTextView.text = chat.msg.trim()
        holder.txtmymsg.text = chat.msg.trim()
        holder.txttimename.text = chat.name+" | "+chat.time
        holder.txtmytimename.text = chat.name+" | "+chat.time
        val name = Chatscreen.sharedPref.getString("name", "Player")
        val uuid = Chatscreen.sharedPref.getString("uuid", "00000")
        Log.d("kool", "onBindViewHolder: ${chat.uuid+"/"+uuid}")

        if(chat.uuid==uuid){
            holder.llincoming.visibility=View.GONE
            holder.lloutgoing.visibility=View.VISIBLE
        }else{
            holder.llincoming.visibility=View.VISIBLE
            holder.lloutgoing.visibility=View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
