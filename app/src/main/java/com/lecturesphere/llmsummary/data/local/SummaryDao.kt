package com.lecturesphere.llmsummary.data.local

import androidx.room.*
import com.lecturesphere.llmsummary.api.models.Summary

@Dao
interface SummaryDao {
    @Query("SELECT * FROM summaries WHERE sessionId = :sessionId")
    suspend fun getSummary(sessionId: String): Summary?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: Summary)
}