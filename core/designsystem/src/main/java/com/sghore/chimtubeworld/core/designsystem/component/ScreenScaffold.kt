package com.sghore.chimtubeworld.core.designsystem.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.chimtubeworld.core.designsystem.R as DesignSystemR

// Each screen owns its title and callbacks; this only preserves the existing chrome appearance.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenScaffold(
    title: String = "CHIMHA",
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.87f),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(DesignSystemR.color.default_background_color),
                    scrolledContainerColor = colorResource(DesignSystemR.color.default_background_color),
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.87f),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.87f),
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                expandedHeight = 56.dp,
                windowInsets = TopAppBarDefaults.windowInsets,
                modifier = Modifier.fillMaxWidth(),
                title = {
                    if (onBack == null) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        // Retain the old 72dp title start with a back icon.
                        Box(Modifier.width(64.dp)) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        }
                    }
                },
                actions = actions
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)) {
            HorizontalDivider(color = colorResource(DesignSystemR.color.gray_bright_night))
            Box(Modifier.weight(1f).fillMaxWidth()) { content() }
        }
    }
}

