package com.example.taructraverse2.ui.chatbot

import android.os.AsyncTask
import com.google.cloud.dialogflow.v2.*

class RequestJavaV2Task(val c: ChatbotFragment, val session: SessionName?, val sessionsClient: SessionsClient?, val queryInput: QueryInput?) :
    AsyncTask<Unit, Unit, DetectIntentResponse>() {

    override fun doInBackground(vararg params: Unit?): DetectIntentResponse? {
        try {
            val detectIntentRequest: DetectIntentRequest = DetectIntentRequest.newBuilder()
                .setSession(session.toString())
                .setQueryInput(queryInput)
                .build()
            return sessionsClient!!.detectIntent(detectIntentRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    override fun onPostExecute(response: DetectIntentResponse?) {
        c.callbackV2(response)
        super.onPostExecute(response)
    }


}