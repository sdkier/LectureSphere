package com.lecturesphere.llmsummary

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.lecturesphere.llmsummary.api.models.ChatMessage
import com.lecturesphere.llmsummary.api.models.ChatSession
import com.lecturesphere.llmsummary.data.SummaryRepository
import com.lecturesphere.llmsummary.data.local.SummaryDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val summaryDao by lazy {
        Room.databaseBuilder(
            applicationContext,
            SummaryDatabase::class.java,
            "summary_database"
        ).build()
            .summaryDao()
    }

    private val summaryRepository by lazy {
        SummaryRepository(summaryDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.testButton).setOnClickListener {
            testSummaryGeneration()
        }
    }

    private fun testSummaryGeneration() {
        lifecycleScope.launch {
            try {
                // Create test chat session
                val testSession = ChatSession(
                    sessionId = "test-session",
                    courseId = "test-course",
                    messages = listOf(
                        ChatMessage(
                            userId = "student1",
                            content = "What is the main theme of Romeo and Juliet?",
                            timestamp = System.currentTimeMillis()
                        ),
                        ChatMessage(
                            userId = "student2",
                            content = "I think it's about forbidden love and family conflict.",
                            timestamp = System.currentTimeMillis()
                        ),
                        ChatMessage(
                            userId = "student3",
                            content = "Yes, and also about how hasty decisions can lead to tragic consequences.",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                )

                // Show loading state
                findViewById<TextView>(R.id.resultText).text = "Generating summary..."

                // Generate summary
                val result = summaryRepository.generateSummary(testSession)

                result.fold(
                    onSuccess = { summary ->
                        Log.d("Test", "Summary generated: ${summary.summary}")
                        findViewById<TextView>(R.id.resultText).text = summary.summary
                    },
                    onFailure = { error ->
                        Log.e("Test", "Error generating summary", error)
                        findViewById<TextView>(R.id.resultText).text = "Error: ${error.message}"
                    }
                )
            } catch (e: Exception) {
                Log.e("Test", "Test failed", e)
                findViewById<TextView>(R.id.resultText).text = "Error: ${e.message}"
            }
        }
    }
}