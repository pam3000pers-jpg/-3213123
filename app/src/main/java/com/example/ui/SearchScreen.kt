package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.api.NewsResult
import com.example.api.WebResult
import com.example.data.BookmarkedResultEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Google Color Palette
val GoogleBlue = Color(0xFF4285F4)
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)

@Composable
fun VibrantBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            color = Color.White.copy(alpha = 0.95f),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFE1E3E1), Color(0xFFE1E3E1).copy(alpha = 0.5f))
                )
            )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Tab
                val interactionSourceHome = remember { MutableInteractionSource() }
                val isPressedHome by interactionSourceHome.collectIsPressedAsState()
                val isSelectedHome = selectedTab == 0
                val scaleHome by animateFloatAsState(
                    targetValue = if (isSelectedHome) 1.15f else if (isPressedHome) 0.95f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "tab_home_scale"
                )
                val paddingHome by animateDpAsState(
                    targetValue = if (isSelectedHome) 22.dp else 14.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "tab_home_padding"
                )
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSourceHome,
                            indication = null
                        ) { onTabSelected(0) }
                        .padding(8.dp)
                        .graphicsLayer(scaleX = scaleHome, scaleY = scaleHome),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelectedHome) Color(0xFFD3E3FD) else Color.Transparent)
                            .padding(horizontal = paddingHome, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (isSelectedHome) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelectedHome) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelectedHome) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f)
                    )
                }

                // Search Tab
                val interactionSourceSearch = remember { MutableInteractionSource() }
                val isPressedSearch by interactionSourceSearch.collectIsPressedAsState()
                val isSelectedSearch = selectedTab == 1
                val scaleSearch by animateFloatAsState(
                    targetValue = if (isSelectedSearch) 1.15f else if (isPressedSearch) 0.95f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "tab_search_scale"
                )
                val paddingSearch by animateDpAsState(
                    targetValue = if (isSelectedSearch) 22.dp else 14.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "tab_search_padding"
                )
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSourceSearch,
                            indication = null
                        ) { onTabSelected(1) }
                        .padding(8.dp)
                        .graphicsLayer(scaleX = scaleSearch, scaleY = scaleSearch),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelectedSearch) Color(0xFFD3E3FD) else Color.Transparent)
                            .padding(horizontal = paddingSearch, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSelectedSearch) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelectedSearch) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelectedSearch) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f)
                    )
                }

                // Saved Tab
                val interactionSourceSaved = remember { MutableInteractionSource() }
                val isPressedSaved by interactionSourceSaved.collectIsPressedAsState()
                val isSelectedSaved = selectedTab == 2
                val scaleSaved by animateFloatAsState(
                    targetValue = if (isSelectedSaved) 1.15f else if (isPressedSaved) 0.95f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "tab_saved_scale"
                )
                val paddingSaved by animateDpAsState(
                    targetValue = if (isSelectedSaved) 22.dp else 14.dp,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                    label = "tab_saved_padding"
                )
                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSourceSaved,
                            indication = null
                        ) { onTabSelected(2) }
                        .padding(8.dp)
                        .graphicsLayer(scaleX = scaleSaved, scaleY = scaleSaved),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelectedSaved) Color(0xFFD3E3FD) else Color.Transparent)
                            .padding(horizontal = paddingSaved, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Saved",
                            tint = if (isSelectedSaved) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Saved",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelectedSaved) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelectedSaved) Color(0xFF041E49) else Color(0xFF444746).copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeQuery by viewModel.activeSearchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchResponse by viewModel.searchResponse.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchError by viewModel.searchError.collectAsStateWithLifecycle()

    val history by viewModel.searchHistory.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarkedResults.collectAsStateWithLifecycle()

    // Dialog state for Voice Search & Google Lens simulation
    var showVoiceDialog by remember { mutableStateOf(false) }
    var showLensDialog by remember { mutableStateOf(false) }

    var activeBottomTab by remember { mutableStateOf(0) } // 0 = Home, 1 = Search, 2 = Saved

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            VibrantBottomNavigation(
                selectedTab = activeBottomTab,
                onTabSelected = { tab ->
                    activeBottomTab = tab
                    if (tab == 0) {
                        viewModel.resetSearch()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeBottomTab) {
                0 -> {
                    HomeView(
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onQueryChanged(it) },
                        onSearchTriggered = {
                            viewModel.triggerSearch(it)
                            activeBottomTab = 1
                        },
                        history = history,
                        onDeleteHistory = { viewModel.deleteHistoryEntry(it) },
                        onClearAllHistory = { viewModel.clearAllHistory() },
                        onVoiceSearchClick = { showVoiceDialog = true },
                        onLensClick = { showLensDialog = true }
                    )
                }
                1 -> {
                    if (activeQuery.isEmpty()) {
                        HomeView(
                            searchQuery = searchQuery,
                            onQueryChanged = { viewModel.onQueryChanged(it) },
                            onSearchTriggered = {
                                viewModel.triggerSearch(it)
                            },
                            history = history,
                            onDeleteHistory = { viewModel.deleteHistoryEntry(it) },
                            onClearAllHistory = { viewModel.clearAllHistory() },
                            onVoiceSearchClick = { showVoiceDialog = true },
                            onLensClick = { showLensDialog = true }
                        )
                    } else {
                        SearchResultsView(
                            activeQuery = activeQuery,
                            searchQuery = searchQuery,
                            onQueryChanged = { viewModel.onQueryChanged(it) },
                            onSearchTriggered = { viewModel.triggerSearch(it) },
                            isSearching = isSearching,
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            searchResponse = searchResponse,
                            searchError = searchError,
                            bookmarks = bookmarks,
                            isBookmarked = { url -> viewModel.isBookmarked(url) },
                            onToggleBookmark = { title, snippet, url -> viewModel.toggleBookmark(title, snippet, url) },
                            onBackClick = {
                                viewModel.resetSearch()
                                activeBottomTab = 0
                            },
                            onVoiceSearchClick = { showVoiceDialog = true },
                            onLensClick = { showLensDialog = true }
                        )
                    }
                }
                2 -> {
                    TabSaved(
                        bookmarks = bookmarks,
                        onToggleBookmark = { title, snippet, url -> viewModel.toggleBookmark(title, snippet, url) }
                    )
                }
            }

            // --- SIMULATED VOICE SEARCH DIALOG ---
            if (showVoiceDialog) {
                SimulatedVoiceSearchDialog(
                    onDismiss = { showVoiceDialog = false },
                    onVoiceResult = { result ->
                        showVoiceDialog = false
                        viewModel.triggerSearch(result)
                        activeBottomTab = 1
                    }
                )
            }

            // --- SIMULATED GOOGLE LENS DIALOG ---
            if (showLensDialog) {
                SimulatedGoogleLensDialog(
                    onDismiss = { showLensDialog = false },
                    onImageSearchTriggered = { label ->
                        showLensDialog = false
                        viewModel.triggerSearch(label)
                        activeBottomTab = 1
                    }
                )
            }
        }
    }
}

@Composable
fun GoogleLogo(modifier: Modifier = Modifier, fontSize: TextUnit = 42.sp) {
    val infiniteTransition = rememberInfiniteTransition(label = "google_logo_glow")
    val waveAnims = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, delayMillis = index * 120, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "letter_$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val letters = listOf(
            "G" to GoogleBlue,
            "o" to GoogleRed,
            "o" to GoogleYellow,
            "g" to GoogleBlue,
            "l" to GoogleGreen,
            "e" to GoogleRed
        )
        letters.forEachIndexed { index, (char, color) ->
            Text(
                text = char,
                color = color,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.offset(y = waveAnims[index].value.dp)
            )
        }
    }
}

data class CategoryItem(val icon: String, val title: String, val query: String, val bgColor: Color, val textColor: Color)

@Composable
fun HomeView(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    history: List<com.example.data.SearchHistoryEntity>,
    onDeleteHistory: (String) -> Unit,
    onClearAllHistory: () -> Unit,
    onVoiceSearchClick: () -> Unit,
    onLensClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- VIBRANT PALETTE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC2E7FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "G",
                        color = Color(0xFF041E49),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "Search",
                    color = Color(0xFF444746),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            // Avatar with gradient
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFB923C), Color(0xFFEC4899))
                        )
                    )
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Google Logo
        GoogleLogo(modifier = Modifier.padding(bottom = 32.dp), fontSize = 56.sp)

        // Rounded Pill Search Bar with Glowing Google Branded Border
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("home_search_bar"),
            shape = RoundedCornerShape(29.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, GoogleBlue)
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { /* click triggers input focus if we had one */ }
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { onSearchTriggered(searchQuery) }
                        )
                    )

                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Поиск в Google...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Voice Search Button
                IconButton(onClick = onVoiceSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = GoogleBlue
                    )
                }

                // Google Lens Button
                IconButton(onClick = onLensClick) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Google Lens",
                        tint = GoogleRed
                    )
                }
            }
        }

        // --- NEW 2026 AI SMART HUB ---
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF3F6FC)
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GoogleBlue.copy(alpha = 0.5f), GoogleGreen.copy(alpha = 0.5f))
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = GoogleBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Нейросетевой Ассистент 2026",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoogleBlue.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "GEMINI PRO",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = GoogleBlue
                        )
                    }
                }

                Text(
                    text = "Запустите умный разбор темы или воспользуйтесь готовыми сценариями ассистента в один клик:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF444746),
                    lineHeight = 16.sp
                )

                // Horizontal scroll of quick AI scenario chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    val aiPrompts = listOf(
                        Triple("✨ Итоги новостей", "news summary latest brief", GoogleBlue),
                        Triple("🌤️ Погода на выходные", "weather forecast and weekend plans", GoogleYellow),
                        Triple("🚀 Технологии 2026", "hottest technology trends in 2026 summary", GoogleGreen),
                        Triple("🎓 Квантовая физика", "explain quantum physics simply to a kid", GoogleRed)
                    )
                    items(aiPrompts) { (label, query, activeColor) ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.94f else 1.0f,
                            label = "ai_chip_scale"
                        )
                        Surface(
                            modifier = Modifier
                                .graphicsLayer(scaleX = scale, scaleY = scale)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { onSearchTriggered(query) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, activeColor.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F1F1F),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- VIBRANT PALETTE QUICK CATEGORIES ---
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val categoryItems = listOf(
                CategoryItem("☀️", "Погода", "weather today", Color(0xFFE8F0FE), Color(0xFF1A73E8)),
                CategoryItem("📰", "Новости", "latest news today", Color(0xFFFEEFC3), Color(0xFFB06000)),
                CategoryItem("⚽", "Спорт", "sports highlights", Color(0xFFE6F4EA), Color(0xFF137333)),
                CategoryItem("🎬", "Фильмы", "popular movies today", Color(0xFFFCE8E6), Color(0xFFC5221F))
            )
            categoryItems.forEach { cat ->
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.90f else 1.0f,
                    label = "category_scale"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onSearchTriggered(cat.query) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(cat.bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.icon,
                            fontSize = 22.sp
                        )
                    }
                    Text(
                        text = cat.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF444746)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SEARCH HISTORY ---
        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История поиска",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onClearAllHistory) {
                    Text(
                        text = "Очистить все",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoogleBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                history.take(5).forEach { historyEntry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSearchTriggered(historyEntry.query) }
                            .padding(vertical = 12.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = historyEntry.query,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = { onDeleteHistory(historyEntry.query) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete entry",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // --- DISCOVER / TRENDS FEED ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Discover",
                tint = GoogleYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Интересное сейчас",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val discoverTopics = listOf(
            DiscoverItem(
                title = "Релиз Jetpack Compose 1.8 Stable: новые возможности анимации",
                category = "Разработка",
                query = "jetpack compose 1.8 stable",
                imageUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400"
            ),
            DiscoverItem(
                title = "Будущее AI в 2026 году: прогнозы экспертов и новые технологии",
                category = "Технологии",
                query = "будущее искусственного интеллекта 2026",
                imageUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=400"
            ),
            DiscoverItem(
                title = "Погода на неделю: аномальное тепло бьет температурные рекорды",
                category = "Погода",
                query = "погода на неделю",
                imageUrl = "https://images.unsplash.com/photo-1592210454359-9043f067919b?w=400"
            )
        )

        discoverTopics.forEach { topic ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1.0f,
                label = "discover_card_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onSearchTriggered(topic.query) },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0xFFF1F3F4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(topic.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = topic.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Glassy / translucent badge overlay
                        Surface(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = topic.category,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Интересное сейчас",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoogleBlue
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = "Read time",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "3 мин чтения",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topic.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

data class DiscoverItem(
    val title: String,
    val category: String,
    val query: String,
    val imageUrl: String
)

@Composable
fun SearchResultsView(
    activeQuery: String,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    isSearching: Boolean,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    searchResponse: com.example.api.SearchResponse?,
    searchError: String?,
    bookmarks: List<BookmarkedResultEntity>,
    isBookmarked: (String) -> Boolean,
    onToggleBookmark: (String, String, String) -> Unit,
    onBackClick: () -> Unit,
    onVoiceSearchClick: () -> Unit,
    onLensClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // --- TOP DOCKED SEARCH BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChanged,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_search_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Search
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onSearch = { onSearchTriggered(searchQuery) }
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChanged("") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onVoiceSearchClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Search",
                            tint = GoogleBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onLensClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Google Lens",
                            tint = GoogleRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // --- SEARCH CATEGORY TABS ---
        val tabs = listOf("Все", "AI Ответ", "Новости", "Картинки", "Сохранено (${bookmarks.size})")
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoogleBlue
                )
            },
            divider = {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            },
            containerColor = Color.Transparent
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) GoogleBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // --- SEARCH CONTENT AREA ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isSearching) {
                // Shimmering Searching State
                SearchingPlaceholder(query = activeQuery)
            } else if (searchError != null) {
                ErrorPlaceholder(error = searchError, onRetry = { onSearchTriggered(activeQuery) })
            } else if (searchResponse != null) {
                when (selectedTab) {
                    0 -> TabAll(
                        query = activeQuery,
                        response = searchResponse,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = onToggleBookmark,
                        onSuggestionClick = onSearchTriggered,
                        onTabSelected = onTabSelected
                    )
                    1 -> TabAiOverview(response = searchResponse)
                    2 -> TabNews(response = searchResponse)
                    3 -> TabImages(query = activeQuery, response = searchResponse)
                    4 -> TabSaved(
                        bookmarks = bookmarks,
                        onToggleBookmark = onToggleBookmark
                    )
                }
            }
        }
    }
}

@Composable
fun SearchingPlaceholder(query: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val loadingPhrases = listOf(
        "Ищем лучшие веб-ресурсы...",
        "Формулируем точный AI-ответ с помощью Gemini...",
        "Анализируем последние новостные ленты...",
        "Подбираем яркие медиафайлы...",
        "Сортируем результаты по релевантности..."
    )

    var currentPhraseIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            currentPhraseIndex = (currentPhraseIndex + 1) % loadingPhrases.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .drawWithContent {
                    drawContent()
                    clipRect {
                        val brush = Brush.sweepGradient(
                            colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, GoogleBlue)
                        )
                        drawCircle(
                            brush = brush,
                            radius = size.minDimension / 2,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 8.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Поиск для: \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = loadingPhrases[currentPhraseIndex],
            style = MaterialTheme.typography.bodyLarge,
            color = GoogleBlue,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.animateContentSize()
        )
    }
}

@Composable
fun ErrorPlaceholder(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Error",
            tint = GoogleRed,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Что-то пошло не так",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue)
        ) {
            Text("Повторить попытку")
        }
    }
}

@Composable
fun TabAll(
    query: String,
    response: com.example.api.SearchResponse,
    isBookmarked: (String) -> Boolean,
    onToggleBookmark: (String, String, String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onTabSelected: (Int) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- AI OVERVIEW MINI-CARD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onTabSelected(1) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Overview",
                                tint = GoogleBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Обзор",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoogleBlue
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Read full",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = response.aiOverview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Читать полностью",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = GoogleBlue
                        )
                    }
                }
            }
        }

        // --- ORGANIC RESULTS HEADER ---
        item {
            Text(
                text = "Результаты поиска",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // --- ORGANIC WEB RESULTS LIST ---
        items(response.webResults) { webResult ->
            WebResultCard(
                webResult = webResult,
                isBookmarked = isBookmarked(webResult.url),
                onBookmarkClick = {
                    onToggleBookmark(webResult.title, webResult.snippet, webResult.url)
                },
                onOpenClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webResult.url))
                    context.startActivity(intent)
                }
            )
        }

        // --- SUGGESTED KEYWORDS ---
        if (response.suggestedKeywords.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = "Вместе с этим ищут:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(response.suggestedKeywords) { keyword ->
                            SuggestionChip(
                                onClick = { onSuggestionClick(keyword) },
                                label = { Text(text = keyword) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WebResultCard(
    webResult: WebResult,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onOpenClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Favicon and domain URL header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Favicon placeholder
                val domainLetter = webResult.url.removePrefix("https://").removePrefix("www.").firstOrNull()?.uppercase() ?: "W"
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GoogleBlue.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = domainLetter.toString(),
                        color = GoogleBlue,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = webResult.url,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) GoogleRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Blue Link Title
            Text(
                text = webResult.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoogleBlue,
                modifier = Modifier.clickable { onOpenClick() }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Snippet Description
            Text(
                text = webResult.snippet,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onOpenClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Link",
                        tint = GoogleBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Открыть",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoogleBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TabAiOverview(response: com.example.api.SearchResponse) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = GoogleBlue.copy(alpha = 0.04f)
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, GoogleBlue.copy(alpha = 0.12f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini",
                        tint = GoogleBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Интеллектуальный AI-обзор",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoogleBlue
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = response.aiOverview,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(response.aiOverview))
                            Toast.makeText(context, "AI Обзор скопирован!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Копировать")
                    }

                    Button(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, response.aiOverview)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Поделиться AI обзором")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Поделиться")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Сгенерировано с помощью Gemini 3.5 Flash. AI может допускать неточности, сверяйте важную информацию.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TabNews(response: com.example.api.SearchResponse) {
    val context = LocalContext.current

    if (response.newsResults.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Newspaper,
                contentDescription = "No news",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Новости не найдены",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(response.newsResults) { news ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://news.google.com"))
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(GoogleRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = news.source.first().toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = news.source,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Text(
                                text = news.time,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = news.snippet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabImages(query: String, response: com.example.api.SearchResponse) {
    val context = LocalContext.current

    // Set up beautiful simulated images depending on keywords in query using Unsplash
    val unsplashPhotoIds = listOf(
        "photo-1555066931-4365d14bab8c", // Technology/code
        "photo-1620712943543-bcc4688e7485", // AI
        "photo-1592210454359-9043f067919b", // Weather
        "photo-1451187580459-43490279c0fa", // Digital globe
        "photo-1518770660439-4636190af475", // Circuit
        "photo-1488590528505-98d2b5aba04b"  // Tech tools
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(unsplashPhotoIds.size) { index ->
            val photoId = unsplashPhotoIds[index]
            val imageUrl = "https://images.unsplash.com/$photoId?w=400&q=80"
            val title = when (index) {
                0 -> "Качественный код на Kotlin"
                1 -> "Визуализация Искусственного Интеллекта"
                2 -> "Климатический анализ и облачность"
                3 -> "Всемирная паутина и базы данных"
                4 -> "Микрочипы и квантовые вычисления"
                else -> "Современные гаджеты и интеграции"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://images.google.com"))
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "unsplash.com",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TabSaved(
    bookmarks: List<BookmarkedResultEntity>,
    onToggleBookmark: (String, String, String) -> Unit
) {
    val context = LocalContext.current

    if (bookmarks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = "No bookmarks",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "В закладках пока ничего нет",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Нажимайте на иконку сердца в результатах поиска, чтобы сохранять полезные ссылки здесь.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Сохраненные результаты",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(bookmarks) { bookmark ->
                WebResultCard(
                    webResult = WebResult(
                        title = bookmark.title,
                        snippet = bookmark.snippet,
                        url = bookmark.url
                    ),
                    isBookmarked = true,
                    onBookmarkClick = {
                        onToggleBookmark(bookmark.title, bookmark.snippet, bookmark.url)
                    },
                    onOpenClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.url))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

// --- SIMULATED VOICE SEARCH DIALOG ---
@Composable
fun SimulatedVoiceSearchDialog(
    onDismiss: () -> Unit,
    onVoiceResult: (String) -> Unit
) {
    val waveAnimations = remember { List(5) { Animatable(0.2f) } }
    
    LaunchedEffect(Unit) {
        waveAnimations.forEachIndexed { index, animatable ->
            launch {
                delay(index * 150L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    var heardText by remember { mutableStateOf("Слушаю...") }
    LaunchedEffect(Unit) {
        delay(1500)
        heardText = "Распознавание..."
        delay(1000)
        heardText = "«Jetpack Compose в Android»"
        delay(800)
        onVoiceResult("Jetpack Compose в Android")
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Голосовой поиск",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoogleBlue,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = heardText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Beautiful voice wave animation
                Row(
                    modifier = Modifier.height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, GoogleBlue)
                    waveAnimations.forEachIndexed { idx, anim ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height((40.dp * anim.value))
                                .clip(RoundedCornerShape(3.dp))
                                .background(colors[idx])
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Скажите, например: «рецепт блинов» или «погода на завтра»",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// --- SIMULATED GOOGLE LENS DIALOG ---
@Composable
fun SimulatedGoogleLensDialog(
    onDismiss: () -> Unit,
    onImageSearchTriggered: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Google Lens",
                        tint = GoogleRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Объектив (Lens)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Выберите объект из галереи камеры для интеллектуального поиска:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                val lensItems = listOf(
                    LensMockItem("Загадочный цветок", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=200", "orchid flower care guide"),
                    LensMockItem("Умные часы", "https://images.unsplash.com/photo-1542496658-e33a6d0d50f6?w=200", "smart watch features"),
                    LensMockItem("Неизвестный код", "https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=200", "kotlin code snippets"),
                    LensMockItem("Эйфелева башня", "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=200", "history of eiffel tower")
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lensItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onImageSearchTriggered(item.searchTag) }
                                .padding(8.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.label,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Нажмите для AI сканирования",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Scan",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть")
                }
            }
        }
    }
}

data class LensMockItem(
    val label: String,
    val imageUrl: String,
    val searchTag: String
)
