package com.example.taructraverse2.ui.chatbot
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.content.Context
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.taructraverse2.R
//import com.google.cloud.dialogflow.v2.Intent
//
//class ChatAdapter(val context: Context, val type:Int) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {
//
//    private val messages: ArrayList<String,String> = ArrayList()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//
//        return if(type == 10001) {
//            MessageViewHolder(layoutInflater.inflate(R.layout.my_message, parent, false))
//
//        } else {
//            MessageViewHolder(layoutInflater.inflate(R.layout.bot_message, parent, false))
//
//        }
//    }
//
//    override fun getItemCount(): Int {
//
//    }
//
//    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
//
//    }
//
//    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
////        val prizeImg = view.findViewById<ImageButton>(R.id.prizeImg)
////        val txtPrize = view.findViewById<TextView>(R.id.prizetxt)
//
//    }
//
//    inner class MyMessageViewHolder (view: View) : MessageViewHolder(view) {
//        private var messageText: TextView = view.txtMyMessage
//        private var timeText: TextView = view.txtMyMessageTime
//
//        override fun bind(message: Message) {
//            messageText.text = message.message
//            timeText.text = DateUtils.fromMillisToTimeString(message.time)
//        }
//    }
//
//    inner class BotMessageViewHolder (view: View) : MessageViewHolder(view) {
//        private var messageText: TextView = view.txtOtherMessage
//        private var userText: TextView = view.txtOtherUser
//        private var timeText: TextView = view.txtOtherMessageTime
//
//        override fun bind(message: Message) {
//            messageText.text = message.message
//            userText.text = message.user
//            timeText.text = DateUtils.fromMillisToTimeString(message.time)
//        }
//    }
//
//
//
//}