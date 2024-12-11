package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class HomePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val db = Firebase.firestore

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Load user information
        db.collection("users").document(TrackedUser.userId).get()
            .addOnSuccessListener { result ->
                val role = result.get("role").toString()
                findViewById<TextView>(R.id.user_email).text = result.get("email").toString()
                val name = sharedPreferences.getString("user_name", "Default Name")
                findViewById<TextView>(R.id.username).text = "$name: $role"
            }

        val classes: ImageButton = findViewById(R.id.btn_classes)
        classes.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        btnChat.setOnClickListener {
            // Redirect to Classes to select a specific class for chat
            val intent = Intent(this, ClassesActivity::class.java)
            Toast.makeText(this, "Select a class to enter its chat room", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        val joinButton = findViewById<Button>(R.id.btn_join_class)
        joinButton.setOnClickListener {
            val classCode = findViewById<EditText>(R.id.input_class_code).text.toString().trim()

            if (classCode.isEmpty()) {
                Toast.makeText(this, "Please enter a class code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = Firebase.firestore
            db.collection("classes").document(classCode)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Add class code to user's enrolled classes
                        db.collection("users").document(TrackedUser.userId)
                            .update("enrolled classes", FieldValue.arrayUnion(classCode))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Successfully joined class!", Toast.LENGTH_SHORT).show()
                                findViewById<EditText>(R.id.input_class_code).text.clear()

                                val intent = Intent(this, ClassesActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error joining class: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Invalid class code", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking class code: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        val settingsButton = findViewById<ImageButton>(R.id.btn_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("user_name", "Default Name")
        findViewById<TextView>(R.id.username).text = savedName
    }

}