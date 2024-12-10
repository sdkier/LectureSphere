package com.example.lecturesphere

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MakeClassActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dynamicTimeContainer: LinearLayout
    private lateinit var btnAddTime: ImageButton
    private lateinit var btnRemoveTime: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeclass)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        // Initialize layout elements
        dynamicTimeContainer = findViewById(R.id.dynamic_time_container)
        btnAddTime = findViewById(R.id.btn_add_time)
        btnRemoveTime = findViewById(R.id.btn_remove_time)

        // Add the first time row
        addTimeRow()

        // Add functionality for + and - buttons
        btnAddTime.setOnClickListener {
            addTimeRow()
        }

        btnRemoveTime.setOnClickListener {
            removeLastTimeRow()
        }

        // Create class button logic
        val btnCreateClass = findViewById<Button>(R.id.btn_create_class)
        btnCreateClass.setOnClickListener {
            val className = findViewById<EditText>(R.id.input_class_name).text.toString().trim()
            if (className.isEmpty()) {
                Toast.makeText(this, "Class name cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save the class details
            saveClass(className)
            Toast.makeText(this, "Class '$className' created!", Toast.LENGTH_SHORT).show()

            // Navigate to ClassesActivity
            val intent = Intent(this, ClassesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun addTimeRow() {
        val inflater = LayoutInflater.from(this)
        val timeRow = inflater.inflate(R.layout.time_row, dynamicTimeContainer, false)
        dynamicTimeContainer.addView(timeRow)
    }

    private fun removeLastTimeRow() {
        if (dynamicTimeContainer.childCount > 1) {
            dynamicTimeContainer.removeViewAt(dynamicTimeContainer.childCount - 1)
        } else {
            Toast.makeText(this, "At least one time row is required!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveClass(className: String) {
        val classes = sharedPreferences.getStringSet("classes", mutableSetOf())?.toMutableSet()
        classes?.add(className)
        sharedPreferences.edit().putStringSet("classes", classes).apply()
    }
}
