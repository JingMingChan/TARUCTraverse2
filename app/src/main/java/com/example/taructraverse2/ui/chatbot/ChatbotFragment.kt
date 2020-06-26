package com.example.taructraverse2.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.taructraverse2.R

class ChatbotFragment : Fragment() {

    private lateinit var chatbotModel: ChatbotModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        chatbotModel =
                ViewModelProviders.of(this).get(ChatbotModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chatbot, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        chatbotModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
