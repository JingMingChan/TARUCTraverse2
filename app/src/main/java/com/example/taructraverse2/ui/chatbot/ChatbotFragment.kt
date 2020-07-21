package com.example.taructraverse2.ui.chatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.taructraverse2.R
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.*
import java.io.InputStream
import java.util.*


class ChatbotFragment : Fragment(){
    //private lateinit var adapter: ChatAdapter
    //private lateinit var messageList:RecyclerView
    private lateinit var chatLayout:LinearLayout
    private lateinit var txtMessage:EditText
    private lateinit var btnSend:Button
    private var sessionsClient: SessionsClient? = null
    private var session: SessionName? = null
    private val uuid: String = UUID.randomUUID().toString()
    private val USER = 10001
    private val BOT = 10002


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chatbot, container, false)

        val scrollview: ScrollView = root.findViewById(R.id.chatScrollView)
        scrollview.post { scrollview.fullScroll(ScrollView.FOCUS_DOWN) }
        chatLayout = root.findViewById(R.id.chatLayout);

        //messageList = root.findViewById(R.id.messageRecycle)
        txtMessage = root.findViewById(R.id.queryEditText)
        btnSend =root.findViewById(R.id.sendBtn)
//        var context = activity as Context
//
//
//        messageList.setHasFixedSize(true)
//        val layoutManager = LinearLayoutManager(context)
//        layoutManager.stackFromEnd = true
//        messageList.layoutManager = LinearLayoutManager(context)


        btnSend.setOnClickListener {
            sendMessage(it)
        }

        return root
    }


    fun initPresenter(){
        try {
            val stream: InputStream = resources.openRawResource(R.raw.taruc_chat)
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
            val projectId: String = (credentials as ServiceAccountCredentials).projectId
            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
            val sessionsSettings: SessionsSettings =
                settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
            sessionsClient = SessionsClient.create(sessionsSettings)
            session = SessionName.of(projectId, uuid)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(view:View){
        val msg = txtMessage.text.toString()

        if(msg.trim().isEmpty()){
            Toast.makeText(context, "Please enter Message!", Toast.LENGTH_LONG).show()
        }else{

            showTextView(msg,USER)
            txtMessage.text.clear()

            val queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en-US")).build()

            RequestJavaV2Task(this, session, sessionsClient, queryInput).execute()
        }
    }

    fun showTextView(message:String,type:Int){

        val layout: FrameLayout? = when (type) {
            USER -> getUserLayout()
            BOT -> getBotLayout()
            else -> getBotLayout()
        }
        layout!!.isFocusableInTouchMode = true
        chatLayout.addView(layout) // move focus to text view to automatically make it scroll up if softfocus

        val tv = layout.findViewById<TextView>(R.id.chatMsg)
        val mesgWithFL = System.getProperty("line.separator")?.let {
            message.replace("\\n",
                it,true)
        }
        tv.text = mesgWithFL
        Linkify.addLinks(tv,Linkify.ALL)
        layout.requestFocus()
        txtMessage.requestFocus()
    }

    fun callbackV2(response: DetectIntentResponse?) {
        if (response != null) { // process aiResponse here
            val botReply = response.queryResult.fulfillmentText

            showTextView(botReply, BOT)
        } else {
            showTextView("There was some communication issue. Please Try again!", BOT)
        }
    }

    @SuppressLint("InflateParams")
    fun getUserLayout(): FrameLayout? {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(R.layout.my_message, null) as FrameLayout?
    }

    @SuppressLint("InflateParams")
    fun getBotLayout(): FrameLayout? {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(R.layout.bot_message, null) as FrameLayout?
    }

    override fun onResume() {
        initPresenter()
        super.onResume()

    }
    override fun onPause() {
        super.onPause()
        sessionsClient?.shutdown()

    }

    override fun onStop() {
        super.onStop()
        sessionsClient?.shutdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionsClient?.shutdown()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sessionsClient?.shutdown()
    }

    override fun onDetach() {
        super.onDetach()
        sessionsClient?.shutdown()
    }
}
