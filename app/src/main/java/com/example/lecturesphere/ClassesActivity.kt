package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ClassesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var noClassesTextView: TextView
    private lateinit var classListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classes_activity)

        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        noClassesTextView = findViewById(R.id.tv_no_classes)
        classListContainer = findViewById(R.id.class_container)

        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        btnChat.setOnClickListener {
            startActivity(Intent(this, LectureChatroomActivity::class.java))
        }

        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        loadClasses()
    }

    private fun loadClasses() {
        val classes = sharedPreferences.getStringSet("classes", setOf())?.toList()

        // Clear the class list container before repopulating
        classListContainer.removeAllViews()

        if (classes.isNullOrEmpty()) {
            noClassesTextView.visibility = View.VISIBLE
            classListContainer.visibility = View.GONE
        } else {
            noClassesTextView.visibility = View.GONE
            classListContainer.visibility = View.VISIBLE

            for (className in classes) {
                val classButton = Button(this).apply {
                    text = className
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 8)
                    }
                    setOnClickListener {
                        // Navigate to the Class Details or Chat Screen
                        Toast.makeText(this@ClassesActivity, "Selected: $className", Toast.LENGTH_SHORT).show()
                    }
                }
                classListContainer.addView(classButton)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload the class list when the activity resumes
        loadClasses()
    }
}
