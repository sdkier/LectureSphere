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
import com.google.firebase.firestore.DocumentChange
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

    private fun getUserInfo(callback: (String, String) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(TrackedUser.userId)
            .get()
            .addOnSuccessListener { document ->
                val userName = document.getString("email") ?: "Unknown"
                val userRole = document.getString("role") ?: "Unknown"
                callback(userName, userRole)
            }
    }

    private fun setupWebSocket() {
        webSocketManager = WebSocketManager.getInstance()
        webSocketManager.setMessageListener { message ->
            activity?.runOnUiThread {
                handleIncomingMessage(message)
            }
        }

        webSocketManager.setConnectionListener { isConnected ->
            activity?.runOnUiThread {
                activity?.findViewById<TextView>(R.id.connection_status)?.text =
                    if (isConnected) "Connected" else "Disconnected"
            }
        }

        classId?.let { cId ->
            webSocketManager.connectToClass(cId)
        }
    }

    private fun setupFirebase() {
        classId?.let { cId ->
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(cId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        println("Error listening for messages: ${error.message}")
                        return@addSnapshotListener
                    }

                    snapshot?.documentChanges?.forEach { change ->
                        when (change.type) {
                            DocumentChange.Type.ADDED -> {
                                activity?.runOnUiThread {
                                    displayMessage(change.document.data)
                                }
                            }
                            else -> {}
                        }
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
        getUserInfo { userName, userRole ->
            val messageMap = mapOf(
                "type" to "message",
                "userId" to TrackedUser.userId,
                "userName" to userName,
                "userRole" to userRole,
                "message" to text,
                "classId" to classId,
                "timestamp" to Date().time
            )

            // Store in Firebase first
            classId?.let { cId ->
                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(cId)
                    .collection("messages")
                    .add(messageMap)
                    .addOnSuccessListener {
                        // Only send via WebSocket after successful Firebase store
                        val messageJson = JSONObject(messageMap).toString()
                        webSocketManager.sendMessage(messageJson)

                        activity?.runOnUiThread {
                            messageInput.text.clear()
                        }
                    }
                    .addOnFailureListener { e ->
                        println("Error storing message: ${e.message}")
                    }
            }
        }
    }

    private fun handleIncomingMessage(messageJson: String) {
        try {
            val message = JSONObject(messageJson)
            // Only display WebSocket messages if they're not from the current user
            // This prevents duplicate messages since Firebase will handle our own messages
            if (message.getString("userId") != TrackedUser.userId) {
                val messageMap = mutableMapOf<String, Any>()
                message.keys().forEach { key ->
                    messageMap[key] = message.get(key)
                }
                activity?.runOnUiThread {
                    displayMessage(messageMap)
                }
            }
        } catch (e: Exception) {
            println("Error handling message: ${e.message}")
        }
    }

    private fun displayMessage(messageData: Map<*, *>) {
        try {
            val messageView = layoutInflater.inflate(R.layout.chat_message_item, messageContainer, false)

            val isMyMessage = messageData["userId"] == TrackedUser.userId
            val messageLayout = messageView.findViewById<LinearLayout>(R.id.message_layout)

            messageLayout?.apply {
                if (isMyMessage) {
                    setBackgroundResource(R.drawable.message_background_sent)
                    gravity = android.view.Gravity.END
                } else {
                    setBackgroundResource(R.drawable.message_background_received)
                    gravity = android.view.Gravity.START
                }
            }

            val userName = messageData["userName"]?.toString() ?: "Unknown"
            val userRole = messageData["userRole"]?.toString() ?: "Unknown"
            messageView.findViewById<TextView>(R.id.message_sender)?.text = "$userName ($userRole)"
            messageView.findViewById<TextView>(R.id.message_text)?.text = messageData["message"]?.toString() ?: ""

            messageContainer.addView(messageView)

            // Scroll to bottom safely
            messageContainer.post {
                (messageContainer.parent as? View)?.scrollTo(0, messageContainer.bottom)
            }
        } catch (e: Exception) {
            println("Error displaying message: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            webSocketManager.closeSocket()
            messageContainer.removeAllViews()
        } catch (e: Exception) {
            println("Error cleaning up: ${e.message}")
        }
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