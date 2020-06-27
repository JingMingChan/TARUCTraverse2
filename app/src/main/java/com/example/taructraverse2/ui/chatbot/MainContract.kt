package com.example.taructraverse2.ui.chatbot


interface MainContract {

    interface View{

    }

    interface Presenter{
        fun sendMessage(message: String)
        fun onDestroy()
    }

}