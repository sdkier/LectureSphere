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
        val classInfo = intent.getStringExtra("CLASS_NAME")
            ?.let { db.collection("classes").document(it) }
        if (classInfo != null) {
            classInfo.get()
                .addOnSuccessListener { result ->
                    findViewById<TextView>(R.id.professor_name).text = "Professor " + result.get("prof name").toString()
                }
        }

        // Initialize the Back button
        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Set up the listener for the Back button
        backButton.setOnClickListener {
            // Navigate back to the Home (MainActivity)
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close this activity to prevent stacking
        }

        // Chat Category Spinner
        val chatCategoriesSpinner = findViewById<Spinner>(R.id.spinner_chat_categories)
        val categories = listOf("General", "Project", "Exam", "Homework")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        chatCategoriesSpinner.adapter = adapter

        val join_discussion = findViewById<Button>(R.id.btn_join_discussion)
        join_discussion.setOnClickListener{
            // Navigate back to Discussion
            val intent = Intent(this, DiscussionListActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close this activity to prevent stacking
        }

        // Spinner Item Selection Listener
        chatCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                // Handle category switch (e.g., load chat messages for the selected category)
                // For now, display the category name in logs
                println("Selected Chat Category: $selectedCategory")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
