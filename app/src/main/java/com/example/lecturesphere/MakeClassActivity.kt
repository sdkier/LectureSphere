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
            finish() // Return to the previous screen
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

            val db = Firebase.firestore
            val classObj = hashMapOf(
                "prof name" to findViewById<EditText>(R.id.input_professor_name).text.toString(),
                "prof email" to findViewById<EditText>(R.id.input_professor_email).text.toString(),
                "time" to findViewById<Spinner>(R.id.spinner_day).selectedItem.toString() + ": " +
                        findViewById<EditText>(R.id.input_start_time).text.toString() + " - " +
                        findViewById<EditText>(R.id.input_end_time).text.toString(),
            )

            db.collection("classes").document(inputClassName.text.toString())
                .set(classObj)
                .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("Failure", "Error writing document", e) }

            db.collection("users").document(TrackedUser.userId)
                .update("enrolled classes", FieldValue.arrayUnion(className))

            // Pass the class name back to ClassesActivity
            val intent = Intent(this, ClassesActivity::class.java)
            intent.putExtra("NEW_CLASS_NAME", className)
            startActivity(intent)
        }
    }

    private fun addTimeRow() {
        val inflater = LayoutInflater.from(this)
        val timeRow = inflater.inflate(R.layout.time_row, dynamicTimeContainer, false)
        dynamicTimeContainer.addView(timeRow)
    }
}
