package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
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

        // Home Nav bar
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Class list nav bar
        val classButton: ImageButton = findViewById(R.id.btn_classes)
        classButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        // Settings nav bar
        val settingsButton = findViewById<ImageButton>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Load existing classes from firebase
        loadClasses()

        // Check if a new class was added
        val newClassName = intent.getStringExtra("NEW_CLASS_NAME")
        if (!newClassName.isNullOrEmpty()) {
            addClassToList(newClassName)
        }
    }

    private fun addClassToList(className: String) {
        val newClassButton = Button(this).apply {
            text = className
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 8)
            }
            setOnClickListener {
                val intent = Intent(this@ClassesActivity, LectureChatroomActivity::class.java)
                intent.putExtra("CLASS_NAME", className)
                startActivity(intent)
            }
        }
        classContainer.addView(newClassButton)
    }

    private fun loadClasses() {
        val db = Firebase.firestore
        db.collection("users").document(TrackedUser.userId).get()
            .addOnSuccessListener { result ->
                val classes = result.get("enrolled classes") as? List<String>
                if (classes != null) {
                    classes.forEach { className ->
                        addClassToList(className)
                    }
                }
            }
    }

}

