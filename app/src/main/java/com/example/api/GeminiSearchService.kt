package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SearchResponse(
    val aiOverview: String,
    val webResults: List<WebResult>,
    val newsResults: List<NewsResult>,
    val suggestedKeywords: List<String>
)

data class WebResult(
    val title: String,
    val snippet: String,
    val url: String
)

data class NewsResult(
    val title: String,
    val source: String,
    val time: String,
    val snippet: String
)

object GeminiSearchService {
    private const val TAG = "GeminiSearchService"
    private const val MODEL = "gemini-3.5-flash"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun performSearch(query: String): SearchResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API Key is placeholder or missing. Using mock generator.")
            return@withContext generateLocalMockResults(query)
        }

        val prompt = """
            You are a real-time smart Google Search engine API. 
            The user has performed the search query: "$query".
            
            Based on your extensive knowledge (up to June 2026), generate high-fidelity search results matching this query.
            Return a JSON object containing the following keys:
            1. "aiOverview": A comprehensive, smart, concise AI overview of the topic answering the query with current facts and insights. (2-4 sentences).
            2. "webResults": An array of 4 objects representing top organic Google search results. Each object must have "title", "snippet", and "url" (make the URLs look realistic).
            3. "newsResults": An array of 3 objects representing top recent news articles. Each object must have "title", "source", "time" (e.g., "2 hours ago" or "Yesterday"), and "snippet".
            4. "suggestedKeywords": An array of 3-4 related search keywords or phrases.
            
            Format strictly as a JSON object matching this schema:
            {
              "aiOverview": "text",
              "webResults": [
                { "title": "text", "snippet": "text", "url": "url" }
              ],
              "newsResults": [
                { "title": "text", "source": "text", "time": "text", "snippet": "text" }
              ],
              "suggestedKeywords": ["text", "text"]
            }
            
            Ensure the JSON is clean, fully valid, and matches standard formatting. Do not wrap the JSON in markdown blocks like ```json ... ```. Just return the raw JSON text.
        """.trimIndent()

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"

            // Construct Gemini REST JSON request manually using org.json
            val root = JSONObject()
            val contents = JSONArray()
            val content = JSONObject()
            val parts = JSONArray()
            val part = JSONObject()
            part.put("text", prompt)
            parts.put(part)
            content.put("parts", parts)
            contents.put(content)
            root.put("contents", contents)

            // Dynamic response MIME type to guarantee JSON output
            val generationConfig = JSONObject()
            generationConfig.put("responseMimeType", "application/json")
            generationConfig.put("temperature", 0.5)
            root.put("generationConfig", generationConfig)

            val requestBody = root.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "API Request failed with code: ${response.code}. Using mock generator.")
                return@withContext generateLocalMockResults(query)
            }

            val bodyText = response.body?.string() ?: ""
            if (bodyText.isEmpty()) {
                return@withContext generateLocalMockResults(query)
            }

            // Parse Gemini REST Response
            val responseJson = JSONObject(bodyText)
            val candidates = responseJson.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val candidateContent = firstCandidate.getJSONObject("content")
            val candidateParts = candidateContent.getJSONArray("parts")
            val textPart = candidateParts.getJSONObject(0).getString("text")

            // Clean markdown blocks if present
            var jsonText = textPart.trim()
            if (jsonText.startsWith("```json")) {
                jsonText = jsonText.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (jsonText.startsWith("```")) {
                jsonText = jsonText.substringAfter("```").substringBeforeLast("```").trim()
            }

            val resultObj = JSONObject(jsonText)
            val aiOverview = resultObj.optString("aiOverview", "Here is your search result for $query.")
            
            val webArray = resultObj.optJSONArray("webResults")
            val webResults = mutableListOf<WebResult>()
            if (webArray != null) {
                for (i in 0 until webArray.length()) {
                    val item = webArray.getJSONObject(i)
                    webResults.add(
                        WebResult(
                            title = item.optString("title", ""),
                            snippet = item.optString("snippet", ""),
                            url = item.optString("url", "")
                        )
                    )
                }
            }

            val newsArray = resultObj.optJSONArray("newsResults")
            val newsResults = mutableListOf<NewsResult>()
            if (newsArray != null) {
                for (i in 0 until newsArray.length()) {
                    val item = newsArray.getJSONObject(i)
                    newsResults.add(
                        NewsResult(
                            title = item.optString("title", ""),
                            source = item.optString("source", ""),
                            time = item.optString("time", ""),
                            snippet = item.optString("snippet", "")
                        )
                    )
                }
            }

            val keywordsArray = resultObj.optJSONArray("suggestedKeywords")
            val suggestedKeywords = mutableListOf<String>()
            if (keywordsArray != null) {
                for (i in 0 until keywordsArray.length()) {
                    suggestedKeywords.add(keywordsArray.getString(i))
                }
            }

            return@withContext SearchResponse(
                aiOverview = aiOverview,
                webResults = webResults,
                newsResults = newsResults,
                suggestedKeywords = suggestedKeywords
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error performing search: ${e.message}. Using fallback mock generator.", e)
            return@withContext generateLocalMockResults(query)
        }
    }

    private fun generateLocalMockResults(query: String): SearchResponse {
        val q = query.lowercase().trim()
        val aiOverview: String
        val webList = mutableListOf<WebResult>()
        val newsList = mutableListOf<NewsResult>()
        val suggestions = mutableListOf<String>()

        if (q.contains("kotlin") || q.contains("compose") || q.contains("android")) {
            aiOverview = "Jetpack Compose is Android's modern, fully declarative toolkit for building native UI. It simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs. The latest releases emphasize enhanced performance, support for adaptive layouts, and seamless Material 3 customization."
            
            webList.add(
                WebResult(
                    title = "Jetpack Compose - Android Developers",
                    snippet = "Get started with Jetpack Compose, Android's modern toolkit for building native UI. Explore interactive layouts, components, and design systems.",
                    url = "https://developer.android.com/compose"
                )
            )
            webList.add(
                WebResult(
                    title = "Compose Layouts, Modifiers and Architecture",
                    snippet = "Learn how to structure layouts in Jetpack Compose, use modifiers to align and pad elements, and implement clean architecture patterns.",
                    url = "https://developer.android.com/develop/ui/compose/layouts"
                )
            )
            webList.add(
                WebResult(
                    title = "Kotlin Programming Language - Official Website",
                    snippet = "Kotlin is a modern, cross-platform, statically typed programming language. It is the recommended standard language for Android development.",
                    url = "https://kotlinlang.org"
                )
            )
            webList.add(
                WebResult(
                    title = "Material 3 Design System for Jetpack Compose",
                    snippet = "Implement Material Design 3 guidelines in Jetpack Compose using modern dynamic colors, typography, and responsive grid layouts.",
                    url = "https://m3.material.io"
                )
            )

            newsList.add(
                NewsResult(
                    title = "Google Announces Jetpack Compose 1.8 Stable Release",
                    source = "TechCrunch",
                    time = "2 hours ago",
                    snippet = "Google has rolled out the stable version of Jetpack Compose 1.8, bringing significant performance improvements, native support for multi-window folding animations, and faster rendering."
                )
            )
            newsList.add(
                NewsResult(
                    title = "Kotlin Multiplatform (KMP) Adoption Surges among Enterprise Apps",
                    source = "ZDNet",
                    time = "Yesterday",
                    snippet = "More than 60% of top Android apps are now actively integrating Kotlin Multiplatform (KMP) into their stack to share business logic with iOS clients."
                )
            )

            suggestions.addAll(
                listOf(
                    "jetpack compose tutorials for beginners",
                    "kotlin multiplatform state management",
                    "compose window size classes adaptive layout",
                    "how to implement room database in compose"
                )
            )
        } else if (q.contains("weather") || q.contains("pogoda") || q.contains("temp")) {
            aiOverview = "The weather for the requested area is currently comfortable with a clear sky. A gentle light breeze is blowing from the east, and humidity is hovering around 52%. High pressure is expected to dominate the region, ensuring stable sunshine for the next 72 hours."
            
            webList.add(
                WebResult(
                    title = "National Weather Service - Live Forecasts",
                    snippet = "Access instant weather forecasts, radar images, severe storm warnings, and climate logs tailored to your exact geolocation.",
                    url = "https://weather.gov"
                )
            )
            webList.add(
                WebResult(
                    title = "Weather Underground - Local Radar Map",
                    snippet = "Weather Underground provides local & long-range weather forecasts, weather reports, maps & tropical weather conditions for locations worldwide.",
                    url = "https://wunderground.com"
                )
            )
            webList.add(
                WebResult(
                    title = "AccuWeather - Global Temperature Forecast",
                    snippet = "Get the local hourly forecast, 15-day outlook, active radar, realfeel temperatures, and allergy reports for your destination.",
                    url = "https://accuweather.com"
                )
            )

            newsList.add(
                NewsResult(
                    title = "Unprecedented Warm Front Sweeps Across Eastern Region",
                    source = "BBC News",
                    time = "1 hour ago",
                    snippet = "Meteorologists confirm an unseasonably warm air mass has settled across major cities, breaking century-old temperature records for the season."
                )
            )
            newsList.add(
                NewsResult(
                    title = "Tropical Storm Formations Decelerate Due to Ocean Current Shift",
                    source = "Reuters",
                    time = "5 hours ago",
                    snippet = "A sudden change in atmospheric pressures has significantly reduced active tropical storm risk along coastal regions, according to marine centers."
                )
            )

            suggestions.addAll(
                listOf(
                    "weather forecast 10 day outlook",
                    "will it rain today in my location",
                    "radar map storm tracker app",
                    "average monthly rainfall chart"
                )
            )
        } else {
            // General Fallback
            aiOverview = "Here is an AI-Synthesized Overview for \"$query\". This query covers general web information and news. Key aspects include educational resources, informational web search directories, and global discussions on related topics. Scroll down to review organic web results, bookmark links, or perform customized search filters."
            
            webList.add(
                WebResult(
                    title = "Wikipedia - Search for \"$query\"",
                    snippet = "Read about \"$query\" on the free encyclopedia. Discover the history, definitions, reference links, and globally documented perspectives.",
                    url = "https://en.wikipedia.org/wiki/Special:Search?search=${query.replace(" ", "_")}"
                )
            )
            webList.add(
                WebResult(
                    title = "Official Reference Portal & Comprehensive Guide",
                    snippet = "A reliable hub containing user manuals, guides, step-by-step instructions, and community-contributed answers related to $query.",
                    url = "https://www.google.com/search?q=${query.replace(" ", "+")}"
                )
            )
            webList.add(
                WebResult(
                    title = "How-To Guide: Best Practices & Essential Tips",
                    snippet = "Explore a comprehensive list of techniques, strategies, common mistakes, and expert recommendations for maximizing results with $query.",
                    url = "https://www.youtube.com/results?search_query=${query.replace(" ", "+")}"
                )
            )
            webList.add(
                WebResult(
                    title = "Quora & Reddit - Popular Discussions on \"$query\"",
                    snippet = "What are people saying about \"$query\"? Read verified user experiences, active threads, recommendations, and popular debates.",
                    url = "https://reddit.com/search/?q=${query.replace(" ", "%20")}"
                )
            )

            newsList.add(
                NewsResult(
                    title = "Global Conversations and Trends Surrounding \"$query\"",
                    source = "Global News Network",
                    time = "4 hours ago",
                    snippet = "Analysts report a massive surge in public interest and search queries for \"$query\" across modern digital communication channels."
                )
            )
            newsList.add(
                NewsResult(
                    title = "New Innovative Tools and Resources for Beginners",
                    source = "Tech Today",
                    time = "Yesterday",
                    snippet = "A series of newly launched tutorials, software integrations, and learning frameworks are making it simpler than ever to master $query."
                )
            )

            suggestions.addAll(
                listOf(
                    "$query tutorial step by step",
                    "what is the meaning of $query",
                    "latest trends in $query 2026",
                    "best resources to learn more about $query"
                )
            )
        }

        return SearchResponse(
            aiOverview = aiOverview,
            webResults = webList,
            newsResults = newsList,
            suggestedKeywords = suggestions
        )
    }
}
