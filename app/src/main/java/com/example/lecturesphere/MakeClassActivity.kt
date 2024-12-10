package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MakeClassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeclass)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish() // Return to the previous screen
        }

        val btnCreateClass = findViewById<Button>(R.id.btn_create_class)
        val inputClassName = findViewById<EditText>(R.id.input_class_name)

        btnCreateClass.setOnClickListener {
            val className = inputClassName.text.toString().trim()

            if (className.isEmpty()) {
                Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass the class name back to ClassesActivity
            val intent = Intent(this, ClassesActivity::class.java)
            intent.putExtra("NEW_CLASS_NAME", className)
            startActivity(intent)
            finish()
        }
    }
}
