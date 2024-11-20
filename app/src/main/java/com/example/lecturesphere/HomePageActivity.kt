package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val classes: ImageButton = findViewById(R.id.btn_classes)
        classes.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        // Set up the listener for Chat button
        btnChat.setOnClickListener {
            // Navigate to the Lecture Chatroom Activity
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }

        val joinButton = findViewById<Button>(R.id.btn_join_class)
        joinButton.setOnClickListener{
            val toastMessage = "Class Joined! Check Class list to view."
            Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
        }

        val settingsButton = findViewById<ImageButton>(R.id.btn_settings)
        settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
