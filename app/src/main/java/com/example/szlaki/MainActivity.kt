package com.example.szlaki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.szlaki.ui.components.AnimatedLogo
import com.example.szlaki.ui.screens.PathDetail
import com.example.szlaki.ui.screens.PathList
import com.example.szlaki.ui.theme.LOGO_PATH_DATA
import com.example.szlaki.ui.theme.PathTheme
import com.example.szlaki.viewmodel.PathViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: PathViewModel = viewModel()
            val systemDark = isSystemInDarkTheme()
            var userDarkTheme by remember { mutableStateOf<Boolean?>(null) }
            val darkTheme = userDarkTheme ?: systemDark

            PathTheme(darkTheme = darkTheme) {
                PathApp(
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    onToggleTheme = { userDarkTheme = !darkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathApp(
    viewModel: PathViewModel,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val pathId = navBackStackEntry?.arguments?.getInt("pathId")
    val chosenPath = pathId?.let { viewModel.getPathById(it) }

    // Loading screen
    if (viewModel.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedLogo(
                modifier = Modifier.size(240.dp, 60.dp),
                pathData = LOGO_PATH_DATA,
                animationDuration = 2000,
                strokeWidth = 4f,
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        if (currentRoute?.startsWith("detail") == true) {
                            Text(
                                text = chosenPath?.name ?: "",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        } else {
                            AnimatedLogo(
                                modifier = Modifier.size(140.dp, 35.dp),
                                pathData = LOGO_PATH_DATA,
                                isAnimated = false,
                                strokeWidth = 2.5f,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Powrót"
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Zmień motyw"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Image(
                    painter = painterResource(id = R.drawable.pathsbg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(if (darkTheme) 0.1f else 0.2f),
                    contentScale = ContentScale.Crop
                )

                NavHost(
                    navController = navController,
                    startDestination = "list",
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    }
                ) {
                    composable("list") {
                        PathList(
                            paths = viewModel.filteredPaths,
                            availableStates = viewModel.availableStates,
                            selectedState = viewModel.selectedState,
                            searchQuery = viewModel.searchQuery,
                            onSearchQueryChange = { viewModel.searchQuery = it },
                            onStateSelected = { state -> viewModel.selectedState = state },
                            onPathClick = { path ->
                                navController.navigate("detail/${path.id}")
                            }
                        )
                    }
                    composable(
                        route = "detail/{pathId}",
                        arguments = listOf(navArgument("pathId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("pathId")
                        val path = id?.let { viewModel.getPathById(it) }
                        if (path != null) {
                            PathDetail(
                                path = path,
                                onBack = { navController.navigateUp() },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        // Floating timer
        if (viewModel.isAnyTimerRunning) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 24.dp)
                    .clickable {
                        viewModel.runningPathId?.let { id ->
                            navController.navigate("detail/$id") {
                                launchSingleTop = true
                            }
                        }
                    },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer aktywny",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
