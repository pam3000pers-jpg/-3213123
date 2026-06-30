package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarked_results")
data class BookmarkedResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val title: String,
    val snippet: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)
