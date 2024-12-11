package com.example.lecturesphere

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LectureChatroomActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val notesGenerator = NotesGenerator(BuildConfig.OPENAI_API_KEY)
    private lateinit var chatCategoriesSpinner: Spinner
    private lateinit var categories: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturechatroom)

        val db = Firebase.firestore
        val classId = intent.getStringExtra("CLASS_NAME")

        // Verify we have a classId before proceeding
        if (classId == null) {
            Toast.makeText(this, "Error: No class selected", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViews(classId, db)
        setupSpinner()
        setupNotesButton(classId)
    }

    private fun setupViews(classId: String, db: com.google.firebase.firestore.FirebaseFirestore) {
        // Load professor information
        db.collection("classes").document(classId).get()
            .addOnSuccessListener { result ->
                findViewById<TextView>(R.id.professor_name).text =
                    "Professor " + (result.get("prof name")?.toString() ?: "Unknown")

                // Only add the chat fragment after successfully loading class info
                supportFragmentManager.beginTransaction()
                    .replace(R.id.chat_container, ChatFragment.newInstance(classId))
                    .commit()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading class: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // Initialize the Back button
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Update connection status
        findViewById<TextView>(R.id.connection_status).text = "Connecting to chat..."
    }

    private fun setupSpinner() {
        chatCategoriesSpinner = findViewById(R.id.spinner_chat_categories)
        categories = listOf("General", "Project", "Exam", "Homework")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chatCategoriesSpinner.adapter = adapter

        chatCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                println("Selected Chat Category: $selectedCategory")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupNotesButton(classId: String) {
        findViewById<ImageButton>(R.id.generate_notes_button).setOnClickListener {
            generateNotesForCurrentThread(classId)
        }
    }

    private fun generateNotesForCurrentThread(classId: String) {
        val loadingDialog = ProgressDialog(this).apply {
            setMessage("Generating notes...")
            setCancelable(false)
            show()
        }

        launch(Dispatchers.IO) {
            try {
                // Fetch all messages without category filtering
                val messages = Firebase.firestore
                    .collection("classes")
                    .document(classId)
                    .collection("messages")
                    .orderBy("timestamp")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.data }

                if (messages.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            this@LectureChatroomActivity,
                            "No messages found to generate notes from",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                println("Found ${messages.size} messages to process")

                // Generate notes
                val notes = notesGenerator.generateNotes(messages)

                // Store the notes in Firebase
                val noteDoc = hashMapOf(
                    "content" to notes,
                    "timestamp" to System.currentTimeMillis(),
                    "classId" to classId
                )

                Firebase.firestore
                    .collection("classes")
                    .document(classId)
                    .collection("notes")
                    .add(noteDoc)
                    .await()

                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    showNotesDialog(notes)
                }

            } catch (e: Exception) {
                println("Error generating notes: ${e.message}")
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        this@LectureChatroomActivity,
                        "Error generating notes: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showNotesDialog(notes: String) {
        AlertDialog.Builder(this)
            .setTitle("Generated Notes")
            .setMessage(notes)
            .setPositiveButton("Copy") { _, _ ->
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("notes", notes)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Notes copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up coroutines when the activity is destroyed
        cancel()
    }
}