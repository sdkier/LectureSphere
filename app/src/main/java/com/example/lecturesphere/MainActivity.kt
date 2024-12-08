package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var role = "Student" //placeholder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val db = Firebase.firestore

        val login = findViewById<Button>(R.id.login_button)
        login.setOnClickListener{
            auth.signInWithEmailAndPassword(findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString(),
                findViewById<EditText>(R.id.editTextTextPassword).text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        TrackedUser.signedInEmail = auth.currentUser?.email
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        val signUp = findViewById<Button>(R.id.sign_up_button)
        signUp.setOnClickListener{
            auth.createUserWithEmailAndPassword(findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString(),
                                            findViewById<EditText>(R.id.editTextTextPassword).text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //val user = auth.currentUser
                        val user = hashMapOf(
                            "email" to findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString(),
                            "role" to role,
                        )
                        db.collection("users")
                            .add(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Success", "DocumentSnapshot added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Failure", "Error adding document", e)
                            }
                        TrackedUser.signedInEmail = auth.currentUser?.email
                        val intent = Intent(this, HomePageActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed." + findViewById<EditText>(R.id.editTextTextEmailAddress).text.toString(),
                            Toast.LENGTH_SHORT,
                        ).show()
                        Log.d("Sign Up Error", findViewById<EditText>(R.id.editTextTextEmailAddress).toString() +
                            findViewById<EditText>(R.id.editTextTextPassword).toString())
                    }
                }
        }

        // TEACHER TA OR STUDENT?
        val roleRadioGroup: RadioGroup = findViewById(R.id.role_radio_group)

        roleRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_teacher -> {
                    // Teacher selected
                    role = "Teacher"
                }
                R.id.radio_ta -> {
                    // TA selected
                    role = "Teachers Assistant"
                }
                R.id.radio_student -> {
                    // Student selected
                    role = "Student"
                }
            }
        }

        // Reference to the Spinner
        val majorSpinner: Spinner = findViewById(R.id.major_spinner)

        // List of majors to populate the Spinner
        val majors = listOf("Business and Management", "Nursing", "Psychology", "Biology", "Engineering",
            "Education", "Communications", "Finance and Accounting", "Criminal Justice", "Anthropology and Sociology",
            "Computer Science", "English", "Economics", "Political Science", "History", "Kinesiology and Physical Therapy",
            "Health Professions", "Art", "Math", "Environmental Science", "Foreign Languages", "Design", "Trades and Personal Services",
            "International Relations", "Chemistry", "Agricultural Sciences", "Information Technology", "Performing Arts",
            "Engineering Technicians", "Food and Nutrition", "Religious Studies", "Film and Photography", "Music", "Physics",
            "Philosophy", "Architecture", "Protective Services", "Legal Studies", "Culinary Arts", "Pharmacy", "Dental Studies",
            "Arts Management", "Veterinary Studies", "Building and Construction")

        // Create an ArrayAdapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, // Default Spinner layout
            majors
        )

        // Set the dropdown layout for the Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Attach the adapter to the Spinner
        majorSpinner.adapter = adapter

    }
}