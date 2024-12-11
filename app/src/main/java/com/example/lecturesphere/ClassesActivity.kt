package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ClassesActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var classContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classes_activity)

        sharedPreferences = getSharedPreferences("classes_prefs", MODE_PRIVATE)
        classContainer = findViewById(R.id.class_container)

        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        val settingsButton = findViewById<ImageButton>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Load existing classes
        loadClasses()

        // Check if a new class was added
        val newClassCode = intent.getStringExtra("NEW_CLASS_CODE")
        if (!newClassCode.isNullOrEmpty()) {
            addClassToList(newClassCode)
        }
    }

    private fun addClassToList(classCode: String) {
        val db = Firebase.firestore
        db.collection("classes").document(classCode)
            .get()
            .addOnSuccessListener { document ->
                val className = document.getString("class name") ?: "Unknown Class"
                val profName = document.getString("prof name") ?: "Unknown Professor"

                val newClassButton = Button(this).apply {
                    text = "$className\nProfessor: $profName\nCode: $classCode"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(16, 8, 16, 8)
                    }
                    setPadding(20, 20, 20, 20)
                    setOnClickListener {
                        val intent = Intent(this@ClassesActivity, LectureChatroomActivity::class.java)
                        intent.putExtra("CLASS_NAME", classCode)
                        startActivity(intent)
                    }
                }
                classContainer.addView(newClassButton)
            }
    }

    private fun loadClasses() {
        val db = Firebase.firestore
        db.collection("users").document(TrackedUser.userId).get()
            .addOnSuccessListener { result ->
                val classCodes = result.get("enrolled classes") as? List<String>
                if (classCodes != null) {
                    classCodes.forEach { classCode ->
                        addClassToList(classCode)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading classes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}