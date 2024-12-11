package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class MakeClassActivity : AppCompatActivity() {
    private lateinit var dynamicTimeContainer: LinearLayout
    private lateinit var btnAddTime: ImageButton
    private lateinit var btnRemoveTime: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeclass)

        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        val btnCreateClass = findViewById<Button>(R.id.btn_create_class)
        val inputClassName = findViewById<EditText>(R.id.input_class_name)
        dynamicTimeContainer = findViewById(R.id.dynamic_time_container)
        btnAddTime = findViewById(R.id.btn_add_time)
        btnRemoveTime = findViewById(R.id.btn_remove_time)

        // Add initial time row
        addTimeRow()

        // adds more rows
        btnAddTime.setOnClickListener {
            addTimeRow()
        }

        // removes a rows
        btnRemoveTime.setOnClickListener {
            if (dynamicTimeContainer.childCount > 1) {
                dynamicTimeContainer.removeViewAt(dynamicTimeContainer.childCount - 1)
            } else {
                Toast.makeText(this, "At least one time is required.", Toast.LENGTH_SHORT).show()
            }
        }

        // Create the new class
        btnCreateClass.setOnClickListener {
            val className = inputClassName.text.toString().trim()
            if (className.isEmpty()) {
                Toast.makeText(this, "Please enter a class name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate a random 8-digit code
            val classCode = String.format("%08d", (0..99999999).random())

            val db = Firebase.firestore
            val classObj = hashMapOf(
                "class name" to className,
                "prof name" to findViewById<EditText>(R.id.input_professor_name).text.toString(),
                "prof email" to findViewById<EditText>(R.id.input_professor_email).text.toString(),
                "time" to findViewById<Spinner>(R.id.spinner_day).selectedItem.toString() + ": " +
                        findViewById<EditText>(R.id.input_start_time).text.toString() + " - " +
                        findViewById<EditText>(R.id.input_end_time).text.toString(),
                "class code" to classCode
            )

            // Store with class code as document ID
            db.collection("classes").document(classCode)
                .set(classObj)
                .addOnSuccessListener {
                    // Add to user's enrolled classes using the class code
                    db.collection("users").document(TrackedUser.userId)
                        .update("enrolled classes", FieldValue.arrayUnion(classCode))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Class created! Code: $classCode", Toast.LENGTH_LONG).show()

            // Pass the class name back to ClassesActivity
            val intent = Intent(this, ClassesActivity::class.java)
            intent.putExtra("NEW_CLASS_NAME", className)
            startActivity(intent)
        }
    }.addOnFailureListener { e ->
                    Log.w("Failure", "Error creating class", e)
                    Toast.makeText(this, "Error creating class: ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }
}

    private fun addTimeRow() {
        val inflater = LayoutInflater.from(this)
        val timeRow = inflater.inflate(R.layout.time_row, dynamicTimeContainer, false)
        dynamicTimeContainer.addView(timeRow)
    }
}
