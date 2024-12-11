package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var spinnerClasses: Spinner
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Home button nav bar
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

        // Chat room in nav bar
        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        btnChat.setOnClickListener {
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }

        // Dark mode switch
        val switchDarkMode = findViewById<Switch>(R.id.switch_dark_mode)
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        spinnerClasses = findViewById(R.id.spinner_classes)
        loadClassesIntoSpinner()

        // Leave class from Firebase
        val btnLeaveClass = findViewById<Button>(R.id.btn_leave_class)
        btnLeaveClass.setOnClickListener {
            val selectedClass = spinnerClasses.selectedItem?.toString()
            if (!selectedClass.isNullOrEmpty()) {
                removeClass(selectedClass)
                showToast("$selectedClass removed")
            } else {
                showToast("No class selected")
            }
        }

        // Make class button
        val btnMakeClass = findViewById<Button>(R.id.btn_make_class)
        btnMakeClass.setOnClickListener {
            val intent = Intent(this, MakeClassActivity::class.java)
            startActivity(intent)
        }

        // Sign out button
        val btnSignOut = findViewById<Button>(R.id.btn_sign_out)
        btnSignOut.setOnClickListener {
            showToast("Signed out successfully")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Save name functionality
        val inputName = findViewById<EditText>(R.id.input_name)
        val btnSaveName = findViewById<Button>(R.id.btn_save_name)

        // Load previously saved name into input field
        inputName.setText(sharedPreferences.getString("user_name", ""))

        btnSaveName.setOnClickListener {
            val name = inputName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveNameToPreferences(name)
                showToast("Name saved successfully!")
            } else {
                showToast("Please enter a valid name")
            }
        }
    }

    private fun loadClassesIntoSpinner() {
        val db = Firebase.firestore
        db.collection("users").document(TrackedUser.userId).get()
            .addOnSuccessListener { result ->
                val classes = result.get("enrolled classes") as? List<String> ?: listOf()
                adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerClasses.adapter = adapter
            }
            .addOnFailureListener { e ->
                showToast("Error loading classes: ${e.message}")
            }
    }

    private fun removeClass(className: String) {
        val db = Firebase.firestore
        db.collection("users").document(TrackedUser.userId)
            .update("enrolled classes", FieldValue.arrayRemove(className))
            .addOnSuccessListener {
                adapter.remove(className)
                adapter.notifyDataSetChanged()
            }
    }

    private fun saveNameToPreferences(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
