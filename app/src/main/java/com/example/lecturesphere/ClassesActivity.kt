package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ClassesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.classes_activity)

        // Initialize the Home button
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            // Navigate back to MainActivity (Home screen)
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close this activity to avoid stacking
        }

        val settingsButton = findViewById<ImageButton>(R.id.btn_settings)
        // Set up the listener for Settings button
        settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        // Set up the listener for Chat button
        btnChat.setOnClickListener {
            // Navigate to the Lecture Chatroom Activity
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }


        // Array of all class buttons
        val classButtons = listOf<Button>(
            findViewById(R.id.btn_class_1),
            findViewById(R.id.btn_class_2),
            findViewById(R.id.btn_class_3),
            findViewById(R.id.btn_class_4),
            findViewById(R.id.btn_class_5),
            findViewById(R.id.btn_class_6),
            findViewById(R.id.btn_class_7),
            findViewById(R.id.btn_class_8)
        )

        // Set up click listener for each button
        for (button in classButtons) {
            button.setOnClickListener {
                // Navigate to Lecture Chatroom Activity
                val intent = Intent(this, LectureChatroomActivity::class.java)
                startActivity(intent)
            }
        }


    }
}
