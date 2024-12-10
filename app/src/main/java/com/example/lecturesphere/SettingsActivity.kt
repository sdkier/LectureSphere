package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var inputName: EditText
    private lateinit var saveNameButton: Button
    private lateinit var spinnerClasses: Spinner
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        val classButton: ImageButton = findViewById(R.id.btn_classes)
        classButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        btnChat.setOnClickListener {
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }

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

        val btnLeaveClass = findViewById<Button>(R.id.btn_leave_class)
        btnLeaveClass.setOnClickListener {
            val selectedClass = spinnerClasses.selectedItem?.toString()
            if (!selectedClass.isNullOrEmpty()) {
                removeClass(selectedClass)
                Toast.makeText(this, "$selectedClass removed", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No class selected", Toast.LENGTH_SHORT).show()
            }
        }

        val btnMakeClass = findViewById<Button>(R.id.btn_make_class)
        btnMakeClass.setOnClickListener {
            val intent = Intent(this, MakeClassActivity::class.java)
            startActivity(intent)
        }

        val btnSignOut = findViewById<Button>(R.id.btn_sign_out)
        btnSignOut.setOnClickListener {
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        inputName = findViewById(R.id.input_name)
        saveNameButton = findViewById(R.id.btn_save_name)

        val savedName = sharedPreferences.getString("user_name", "")
        inputName.setText(savedName)

        saveNameButton.setOnClickListener {
            val name = inputName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveName(name)
                Toast.makeText(this, "Name saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadClassesIntoSpinner() {
        val classes = sharedPreferences.getStringSet("classes", setOf())?.toMutableList() ?: mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClasses.adapter = adapter
    }

    private fun removeClass(className: String) {
        val classes = sharedPreferences.getStringSet("classes", mutableSetOf())?.toMutableSet()
        if (classes?.remove(className) == true) {
            sharedPreferences.edit().putStringSet("classes", classes).apply()
            adapter.remove(className)
            adapter.notifyDataSetChanged()
        }
    }

    private fun saveName(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }
}
