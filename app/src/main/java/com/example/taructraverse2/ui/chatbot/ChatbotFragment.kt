package com.example.taructraverse2.ui.chatbot

import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.android.AIService
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taructraverse2.R
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.dialogflow.v2.SessionName
import com.google.cloud.dialogflow.v2.SessionsClient
import com.google.cloud.dialogflow.v2.SessionsSettings
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream


class ChatbotFragment : Fragment(),MainContract.View {
    private lateinit var adapter: ChatAdapter
    private lateinit var messageList:RecyclerView
    private lateinit var txtMessage:EditText
    private lateinit var btnSend:Button
    lateinit var ref: DatabaseReference
    lateinit var aiService: AIService
    lateinit var aiDataAIService: AIDataService
    var user: String? = null
    lateinit var mPresenter : MainContract.Presenter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_chatbot, container, false)

        initPresenter()
        messageList = root.findViewById(R.id.messageRecycle)
        txtMessage = root.findViewById(R.id.messageEdit)
        btnSend =root.findViewById(R.id.sendBtn)
        var context = activity as Context

        messageList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        messageList.layoutManager = LinearLayoutManager(context!!)

        ref.keepSynced(true)

        btnSend.setOnClickListener {
            val message = txtMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                mPresenter.sendMessage(message)
            } else {
                aiService.startListening()
                Toast.makeText(context, "Enter message first", Toast.LENGTH_SHORT).show()
            }
            txtMessage.setText("")
        }

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(ref.child("chat"), ChatMessage::class.java)
            .build()

        adapter = ChatAdapter(options)

        messageList.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val msgCount = adapter.itemCount
                val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if (lastVisiblePosition == -1 || positionStart >= msgCount - 1 && lastVisiblePosition == positionStart - 1) {
                    messageList.scrollToPosition(positionStart)
                }
            }
        })

        return root
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
        mPresenter.onDestroy()
    }

    fun initPresenter(){
        val aiConfiguration = ai.api.android.AIConfiguration("beb87e5e512c599a62e9e52da1781cccb9a36d95",
            AIConfiguration.SupportedLanguages.English,
            ai.api.android.AIConfiguration.RecognitionEngine.System)

        var context = activity as Context
        aiService = AIService.getService(context, aiConfiguration)
        aiDataAIService = AIDataService(aiConfiguration)
        ref = FirebaseDatabase.getInstance().reference

        mPresenter = MainPresenter(aiDataAIService, ref)

    }

//    fun initPresenter2(){
//        try {
//            val stream: InputStream = resources.openRawResource(R.raw.taruc_traverse_0d2eaf8ff0b1)
//            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
//            val projectId: String = (credentials as ServiceAccountCredentials).getProjectId()
//            val settingsBuilder: SessionsSettings.Builder = SessionsSettings.newBuilder()
//            val sessionsSettings: SessionsSettings =
//                settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//                    .build()
//            val sessionsClient = SessionsClient.create(sessionsSettings)
//            val session = SessionName.of(projectId, "1")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}
