package com.example.lecturesphere

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.util.*

private fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    this.keys().forEach { key ->
        when (val value = this.get(key.toString())) {
            is JSONObject -> map[key.toString()] = value.toMap()
            JSONObject.NULL -> map[key.toString()] = ""
            else -> map[key.toString()] = value
        }
    }
    return map
}

class ChatFragment : Fragment() {
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var messageContainer: LinearLayout
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private var classId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageContainer = view.findViewById(R.id.message_container)
        messageInput = view.findViewById(R.id.message_input)
        sendButton = view.findViewById(R.id.send_button)

        classId = arguments?.getString("CLASS_ID")

        setupWebSocket()
        setupFirebase()
        setupUI()
    }

    private fun setupWebSocket() {
        webSocketManager = WebSocketManager.getInstance()
        webSocketManager.setMessageListener { message ->
            activity?.runOnUiThread {
                handleIncomingMessage(message)
            }
        }
        webSocketManager.startSocket()
    }

    private fun setupFirebase() {
        // Loading the messages from Firebase. Using o(1) indexing so should be fast. COME BACK TO THIS PART!!!
        classId?.let { classId ->
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, e ->
                    snapshot?.documents?.forEach { doc ->
                        val message = doc.data
                        message?.let { displayMessage(it) }
                    }
                }
        }
    }

    private fun setupUI() {
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }
    }

    private fun sendMessage(text: String) {
        val message = JSONObject().apply {
            put("type", "message")
            put("userId", TrackedUser.userId)
            put("message", text)
            put("classId", classId)
            put("timestamp", Date().time)
        }

        // Send the message over the WebSocket
        webSocketManager.sendMessage(message.toString())

        //Store the msg in Firebase
        classId?.let { classId ->
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classId)
                .collection("messages")
                .add(message.toMap())
        }
    }

    private fun handleIncomingMessage(messageJson: String) {
        val message = JSONObject(messageJson)
        displayMessage(message.toMap())
    }

    private fun displayMessage(messageData: Map<*, *>) {
        val messageView = layoutInflater.inflate(R.layout.chat_message_item, messageContainer, false)

        val isMyMessage = messageData["userId"] == TrackedUser.userId
        val messageLayout = messageView.findViewById<LinearLayout>(R.id.message_layout)
        messageLayout.apply {
            if (isMyMessage) {
                setBackgroundResource(R.drawable.message_background_sent)
                gravity = android.view.Gravity.END
            } else {
                setBackgroundResource(R.drawable.message_background_received)
                gravity = android.view.Gravity.START
            }
        }

        messageView.findViewById<TextView>(R.id.message_text).text = messageData["message"].toString()
        messageContainer.addView(messageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.closeSocket()
    }

    companion object {
        fun newInstance(classId: String): ChatFragment {
            return ChatFragment().apply {
                arguments = Bundle().apply {
                    putString("CLASS_ID", classId)
                }
            }
        }
    }
}