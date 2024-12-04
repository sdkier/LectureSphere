package com.lecturesphere.llmsummary.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lecturesphere.llmsummary.api.models.Summary

@Database(entities = [Summary::class], version = 1)
abstract class SummaryDatabase : RoomDatabase() {
    abstract fun summaryDao(): SummaryDao

    companion object {
        @Volatile
        private var INSTANCE: SummaryDatabase? = null

        fun getDatabase(context: Context): SummaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SummaryDatabase::class.java,
                    "summary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}