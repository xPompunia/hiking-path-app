package com.example.szlaki.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.szlaki.model.Path
import com.example.szlaki.ui.components.PathItem

@Composable
fun PathList(paths: List<Path>, onPathClick: (Path) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(paths) { path ->
            PathItem(path, onClick = { onPathClick(path) })
        }
    }
}
