package com.example.data

import kotlinx.coroutines.flow.Flow

class SearchRepository(private val searchDao: SearchDao) {
    val searchHistory: Flow<List<SearchHistoryEntity>> = searchDao.getSearchHistory()
    val bookmarkedResults: Flow<List<BookmarkedResultEntity>> = searchDao.getBookmarkedResults()

    suspend fun insertHistory(query: String) {
        searchDao.insertSearchHistory(SearchHistoryEntity(query = query))
    }

    suspend fun deleteHistory(query: String) {
        searchDao.deleteSearchHistoryEntry(query)
    }

    suspend fun clearHistory() {
        searchDao.clearAllHistory()
    }

    suspend fun insertBookmark(query: String, title: String, snippet: String, url: String) {
        searchDao.insertBookmark(
            BookmarkedResultEntity(
                query = query,
                title = title,
                snippet = snippet,
                url = url
            )
        )
    }

    suspend fun deleteBookmark(url: String) {
        searchDao.deleteBookmarkByUrl(url)
    }

    fun isBookmarked(url: String): Flow<Boolean> {
        return searchDao.isBookmarked(url)
    }
}
