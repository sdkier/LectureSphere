package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

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
            finish()
        }
    }
}
