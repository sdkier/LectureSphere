package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val login = findViewById<Button>(R.id.login_button)
        login.setOnClickListener{
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        // TEACHER TA OR STUDENT?
        val roleRadioGroup: RadioGroup = findViewById(R.id.role_radio_group)

        roleRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_teacher -> {
                    // Teacher selected
                }
                R.id.radio_ta -> {
                    // TA selected
                }
                R.id.radio_student -> {
                    // Student selected
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