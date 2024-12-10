package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomePageActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usernameTextView: TextView
    private lateinit var userEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        usernameTextView = findViewById(R.id.username)
        userEmailTextView = findViewById(R.id.user_email)

        val defaultUsername = getString(R.string.default_username)
        val savedUsername = sharedPreferences.getString("user_name", defaultUsername)
        usernameTextView.text = savedUsername

        val db = Firebase.firestore
        db.collection("users").document(TrackedUser.userId).get()
            .addOnSuccessListener { result ->
                userEmailTextView.text = result.getString("email") ?: "useremail@email.com"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }

        findViewById<ImageButton>(R.id.btn_classes).setOnClickListener {
            startActivity(Intent(this, ClassesActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_chat).setOnClickListener {
            startActivity(Intent(this, LectureChatroomActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btn_join_class).setOnClickListener {
            Toast.makeText(this, "Class Joined! Check Class list to view.", Toast.LENGTH_SHORT).show()
        }
    }
}
