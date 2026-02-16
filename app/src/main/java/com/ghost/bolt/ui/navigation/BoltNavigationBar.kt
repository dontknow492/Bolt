package com.ghost.bolt.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey

data class BoltNavigationBarItem(
    val label: String,
    val icon: @Composable () -> Unit,
    val selectedIcon: @Composable () -> Unit,
    val key: NavKey,
)

@Composable
fun BoltNavigationBar(
    modifier: Modifier = Modifier,
    items: List<BoltNavigationBarItem>,
    selected: NavKey,
    onItemClick: (NavKey) -> Unit,
) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.key == selected,
                onClick = { onItemClick(item.key) },
                icon = if (item.key == selected) item.selectedIcon else item.icon,
                label = { Text(text = item.label) }
            )
        }
    }
}