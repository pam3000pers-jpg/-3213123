package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchHistoryEntity::class, BookmarkedResultEntity::class], version = 1, exportSchema = false)
abstract class SearchDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao

    companion object {
        @Volatile
        private var INSTANCE: SearchDatabase? = null

        fun getDatabase(context: Context): SearchDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "google_search_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
