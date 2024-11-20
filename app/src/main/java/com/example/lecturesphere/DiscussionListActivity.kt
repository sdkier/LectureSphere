package com.example.lecturesphere

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*


class DiscussionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discussion_list)

        // Initialize the Back button
        val backButton = findViewById<ImageButton>(R.id.back_button)

        // Set up the listener for the Back button
        backButton.setOnClickListener {
            // Navigate back to the Home (MainActivity)
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish() // Optional: Close this activity to prevent stacking
        }

        // Get the container for discussion items
        val discussionContainer = findViewById<LinearLayout>(R.id.discussion_list_container)

        // Generate today's date and the previous 10 days
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()


        // Add discussions for today and the previous 10 days
        for (i in 0..10) {
            // Format the current date
            val formattedDate = dateFormat.format(calendar.time)

            // Create a new TextView for each discussion box
            val discussionBox = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    200 // Height for the square box
                ).apply {
                    setMargins(0, 8, 0, 8) // Add spacing between boxes
                }
                text = "Discussion for $formattedDate"
                textSize = 16f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.border_background) // Add a border drawable
                setPadding(16, 16, 16, 16)
            }

            // Add the discussion box to the container
            discussionContainer.addView(discussionBox)

            // Move to the previous day
            calendar.add(Calendar.DATE, -1)
        }
    }
}
