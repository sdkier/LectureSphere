package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Top Navigation Bar Buttons

        // Home Switcher
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        btnHome.setOnClickListener {
            // Navigate back to MainActivity (Home screen)
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close this activity to avoid stacking
        }

        // Class Switcher
        val classButton: ImageButton = findViewById(R.id.btn_classes)
        classButton.setOnClickListener {
            val intent = Intent(this, ClassesActivity::class.java)
            startActivity(intent)
        }

        // Chat Switcher
        val btnChat = findViewById<ImageButton>(R.id.btn_chat)
        // Set up the listener for Chat button
        btnChat.setOnClickListener {
            // Navigate to the Lecture Chatroom Activity
            val intent = Intent(this, LectureChatroomActivity::class.java)
            startActivity(intent)
        }

        // Dark Mode Switch
        val switchDarkMode = findViewById<Switch>(R.id.switch_dark_mode)
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Font Size Adjustment
        val seekBarFontSize = findViewById<SeekBar>(R.id.seekbar_font_size)
        val textFontSizePreview = findViewById<TextView>(R.id.text_font_size_preview)
        seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val fontSize = progress.toFloat()
                textFontSizePreview.textSize = fontSize
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Populate Classes Spinner
        val spinnerClasses = findViewById<Spinner>(R.id.spinner_classes)
        val classes = listOf("Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClasses.adapter = adapter

        // Leave Class Button
        val btnLeaveClass = findViewById<Button>(R.id.btn_leave_class)
        btnLeaveClass.setOnClickListener {
            val selectedClass = spinnerClasses.selectedItem.toString()
            // Perform logic to leave the selected class
            // For now, show a simple toast
            showToast("Left $selectedClass")
        }
    }

    // Helper function to show toast messages
    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
