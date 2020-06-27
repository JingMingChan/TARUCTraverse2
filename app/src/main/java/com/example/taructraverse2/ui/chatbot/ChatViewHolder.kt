package com.example.taructraverse2.ui.chatbot


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taructraverse2.R


class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var userText : TextView = itemView.findViewById(R.id.userText) as TextView
    var botText  :TextView = itemView.findViewById(R.id.botText) as TextView

}
