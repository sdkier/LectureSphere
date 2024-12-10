package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LectureChatroomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturechatroom)

        val db = Firebase.firestore
        val classId = intent.getStringExtra("CLASS_NAME")

        // Load professor information
        if (classId != null) {
            db.collection("classes").document(classId).get()
                .addOnSuccessListener { result ->
                    findViewById<TextView>(R.id.professor_name).text = "Professor " + result.get("prof name").toString()
                }

            // Add chat fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.chat_container, ChatFragment.newInstance(classId))
                .commit()
        }

        // Initialize the Back button
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Chat Category Spinner
        val chatCategoriesSpinner = findViewById<Spinner>(R.id.spinner_chat_categories)
        val categories = listOf("General", "Project", "Exam", "Homework")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chatCategoriesSpinner.adapter = adapter

        // Join Discussion Button
        val join_discussion = findViewById<Button>(R.id.btn_join_discussion)
        join_discussion.setOnClickListener {
            val intent = Intent(this, DiscussionListActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Spinner Item Selection Listener
        chatCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                // You can add functionality to filter chat messages by category here
                println("Selected Chat Category: $selectedCategory")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}