package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

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

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        btnChat.setOnClickListener {
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }

        // Load existing classes from SharedPreferences
        loadClasses()

        // Check if a new class was added
        val newClassName = intent.getStringExtra("NEW_CLASS_NAME")
        if (!newClassName.isNullOrEmpty()) {
            addClassToList(newClassName)
            saveClass(newClassName)
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
                startActivity(intent)
            }
        }
        classContainer.addView(newClassButton)
    }

    private fun saveClass(className: String) {
        val classes = sharedPreferences.getStringSet("classes", mutableSetOf())?.toMutableSet()
        classes?.add(className)
        sharedPreferences.edit().putStringSet("classes", classes).apply()
    }

    private fun loadClasses() {
        val classes = sharedPreferences.getStringSet("classes", setOf())
        classes?.forEach { className ->
            addClassToList(className)
        }
    }
}
