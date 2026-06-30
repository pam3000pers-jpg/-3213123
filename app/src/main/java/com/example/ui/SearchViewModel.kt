package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiSearchService
import com.example.api.SearchResponse
import com.example.data.BookmarkedResultEntity
import com.example.data.SearchDatabase
import com.example.data.SearchHistoryEntity
import com.example.data.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SearchRepository

    init {
        val database = SearchDatabase.getDatabase(application)
        repository = SearchRepository(database.searchDao())
    }

    // --- Search States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeSearchQuery = MutableStateFlow("")
    val activeSearchQuery: StateFlow<String> = _activeSearchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResponse = MutableStateFlow<SearchResponse?>(null)
    val searchResponse: StateFlow<SearchResponse?> = _searchResponse.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: All, 1: AI Overview, 2: Images, 3: News, 4: Saved Bookmarks
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    // --- History & Bookmarks ---
    val searchHistory: StateFlow<List<SearchHistoryEntity>> = repository.searchHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedResults: StateFlow<List<BookmarkedResultEntity>> = repository.bookmarkedResults
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun triggerSearch(query: String) {
        if (query.trim().isEmpty()) return
        
        _searchQuery.value = query
        _activeSearchQuery.value = query
        _isSearching.value = true
        _searchError.value = null
        
        viewModelScope.launch {
            // Save to local Room history
            repository.insertHistory(query)
            
            try {
                val response = GeminiSearchService.performSearch(query)
                _searchResponse.value = response
            } catch (e: Exception) {
                _searchError.value = "Failed to fetch results: ${e.localizedMessage}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun deleteHistoryEntry(query: String) {
        viewModelScope.launch {
            repository.deleteHistory(query)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun toggleBookmark(title: String, snippet: String, url: String) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = bookmarkedResults.value.any { it.url == url }
            if (isCurrentlyBookmarked) {
                repository.deleteBookmark(url)
            } else {
                repository.insertBookmark(
                    query = _activeSearchQuery.value.ifEmpty { "General Search" },
                    title = title,
                    snippet = snippet,
                    url = url
                )
            }
        }
    }

    fun isBookmarked(url: String): Boolean {
        return bookmarkedResults.value.any { it.url == url }
    }

    fun resetSearch() {
        _searchQuery.value = ""
        _activeSearchQuery.value = ""
        _searchResponse.value = null
        _searchError.value = null
        _selectedTab.value = 0
    }
}
