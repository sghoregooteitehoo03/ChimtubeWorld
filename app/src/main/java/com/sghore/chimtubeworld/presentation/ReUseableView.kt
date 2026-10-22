package com.sghore.chimtubeworld.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.sghore.chimtubeworld.navigation.TOP_LEVEL_DESTINATIONS
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

@Composable
fun BottomNavigationBar(currentTopLevelKey: NavKey, onDestinationClick: (NavKey) -> Unit) {
    NavigationBar(
        modifier = Modifier.height(56.dp).shadow(8.dp),
        containerColor = colorResource(DesignSystemR.color.default_background_color),
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Row(Modifier.fillMaxSize()) {
            TOP_LEVEL_DESTINATIONS.forEach { destination ->
                val selected = destination.key == currentTopLevelKey
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onBackground,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        indicatorColor = Color.Transparent
                    ),
                    icon = {
                        Image(
                            painterResource(if (selected) destination.selectedIconRes else destination.unselectedIconRes),
                            contentDescription = null
                        )
                    },
                    selected = selected,
                    onClick = { onDestinationClick(destination.key) }
                )
            }
        }
    }
}
