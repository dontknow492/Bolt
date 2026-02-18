package com.ghost.bolt.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ghost.bolt.R
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.screen.HomeScreen
import com.ghost.bolt.ui.screen.MediaGrid
import com.ghost.bolt.ui.screen.SearchScreen
import com.ghost.bolt.ui.screen.detail.DetailedMediaScreen
import kotlinx.serialization.Serializable

@Serializable
object MovieHomeKey : NavKey

@Serializable
object TVHomeKey : NavKey

@Serializable
object SearchKey : NavKey

@Serializable
data class SeeAllKey(
    val category: AppCategory,
    val mediaType: AppMediaType,
    val mediaSource: MediaSource
) : NavKey


@Serializable
data class DetailMediaKey(
    val media: MediaCardUiModel
) : NavKey


val navigationBarItem = listOf(
    BoltNavigationBarItem(
        label = "Home",
        icon = {
            Icon(
                painter = painterResource(R.drawable.video_library),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        selectedIcon = {
            Icon(
                painter = painterResource(R.drawable.video_library_filled),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        key = MovieHomeKey
    ),
    BoltNavigationBarItem(
        label = "Search",
        icon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        selectedIcon = {
            Icon(
                painter = painterResource(R.drawable.search_star),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        key = SearchKey
    ),
    BoltNavigationBarItem(
        label = "TV Shows",
        icon = {
            Icon(
                painter = painterResource(R.drawable.tv),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        selectedIcon = {
            Icon(
                painter = painterResource(R.drawable.connected_tv),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        key = TVHomeKey
    )
)

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(MovieHomeKey)

    val isBottomBarVisible = remember(backStack.size) {
        val topKey = backStack.lastOrNull()
        topKey is MovieHomeKey || topKey is TVHomeKey || topKey is SearchKey
    }

    var selected by remember { mutableStateOf<NavKey>(TVHomeKey) }
    val onSelected: (NavKey) -> Unit = { key ->
        selected = key

        if (key in backStack) {
            backStack.remove(key)
        }
        backStack.add(key)

    }

    val onBackClick: () -> Unit = {
        backStack.removeLastOrNull()
        if (backStack.isEmpty()) {
            selected = TVHomeKey
        }
    }

    val onMediaClick: (media: MediaCardUiModel) -> Unit = { media ->
        backStack.add(DetailMediaKey(media))
    }

    val onSeeAllClick: (category: AppCategory, mediaType: AppMediaType, mediaSource: MediaSource) -> Unit =
        { category, mediaType, mediaSource ->
            backStack.add(SeeAllKey(category, mediaType, mediaSource))
        }




    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(
                    initialOffsetY = { it }, // Start from the very bottom (full height offset)
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 300 // Delay before sliding up
                    )
                ) + fadeIn(animationSpec = tween(300)), // Optional: add a fade for a smoother look
                exit = slideOutVertically(
                    targetOffsetY = { it }, // Slide down to the very bottom
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 300 // Usually, you want the exit to start immediately
                    )
                ) + fadeOut(animationSpec = tween(300))
            ) {
                BoltNavigationBar(
                    items = navigationBarItem,
                    selected = selected,
                    onItemClick = onSelected
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack,
            modifier = modifier,
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSaveableStateHolderNavEntryDecorator(),
                // Then add the view model store decorator
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = { key ->
                when (key) {
                    is MovieHomeKey -> {
                        NavEntry(key = key) {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onMediaClick = onMediaClick,
                                mediaType = AppMediaType.MOVIE,
                                mediaSource = MediaSource.TMDB,
                                onSeeAllClick = onSeeAllClick,
                            )
                        }
                    }

                    is TVHomeKey -> {
                        NavEntry(key = key) {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onMediaClick = onMediaClick,
                                mediaType = AppMediaType.TV,
                                mediaSource = MediaSource.TMDB,
                                onSeeAllClick = onSeeAllClick,
                            )
                        }
                    }

                    is SearchKey -> {
                        NavEntry(key = key) {
                            SearchScreen(modifier = Modifier.padding(innerPadding))
                        }
                    }

                    is DetailMediaKey -> {
                        NavEntry(key = key) {
                            DetailedMediaScreen(
                                media = key.media,
                                modifier = Modifier.fillMaxSize(),
                                onBackClick = onBackClick,
                                onCastClick = { /*TODO*/ },
                                onMediaClick = onMediaClick,
                                onGenreClick = { /*TODO*/ },
                                onKeywordClick = { /*TODO*/ }
                            )
                        }
                    }

                    is SeeAllKey -> {
                        NavEntry(key = key) {
                            MediaGrid(
                                onMediaClick = onMediaClick,
                                mediaType = key.mediaType,
                                mediaSource = key.mediaSource,
                                category = key.category,
                                onBackClick = onBackClick
                            )
                        }
                    }

                    else -> error("Unknown key: $key")
                }
            }
        )
    }
}

