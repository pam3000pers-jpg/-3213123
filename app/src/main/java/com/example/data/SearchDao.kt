package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    // --- History ---
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchHistoryEntry(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarked_results ORDER BY timestamp DESC")
    fun getBookmarkedResults(): Flow<List<BookmarkedResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkedResultEntity)

    @Query("DELETE FROM bookmarked_results WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_results WHERE url = :url LIMIT 1)")
    fun isBookmarked(url: String): Flow<Boolean>
}
