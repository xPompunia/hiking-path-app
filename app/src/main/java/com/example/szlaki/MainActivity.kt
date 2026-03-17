package com.example.szlaki

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.szlaki.model.Path
import com.example.szlaki.ui.screens.PathDetail
import com.example.szlaki.ui.screens.PathList
import com.example.szlaki.ui.theme.CustomFont
import com.example.szlaki.ui.theme.PathTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PathTheme {
                PathApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathApp() {
    val context = LocalContext.current
    var paths by remember { mutableStateOf<List<Path>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var chosenPath by remember { mutableStateOf<Path?>(null) }

    LaunchedEffect(Unit) {
        paths = loadPathsFromAssets(context)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = chosenPath?.name ?: "Moje Szlaki",
                        fontFamily = CustomFont,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                navigationIcon = {
                    if (chosenPath != null) {
                        IconButton(onClick = { chosenPath = null }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Powrót"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.7f)
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Image(
                painter = painterResource(id = R.drawable.pathsbg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.2f),
                contentScale = ContentScale.Crop
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (chosenPath == null) {
                PathList(paths = paths, onPathClick = { path -> chosenPath = path })
            } else {
                PathDetail(path = chosenPath!!, onBack = { chosenPath = null })
            }
        }
    }
}

fun loadPathsFromAssets(context: Context): List<Path> {
    return try {
        val jsonString = context.assets.open("szlaki.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<Path>>() {}.type
        Gson().fromJson(jsonString, listType)
    } catch (e: Exception) {
        emptyList()
    }
}
