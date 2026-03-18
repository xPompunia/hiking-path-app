package com.example.szlaki.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.szlaki.model.Path
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PathViewModel(application: Application) : AndroidViewModel(application) {
    var paths by mutableStateOf<List<Path>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(true)
        private set

    init {
        loadPaths()
    }

    private fun loadPaths() {
        viewModelScope.launch {
            isLoading = true
            paths = withContext(Dispatchers.IO) {
                try {
                    val jsonString = getApplication<Application>().assets.open("szlaki.json").bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Path>>() {}.type
                    Gson().fromJson(jsonString, listType)
                } catch (e: Exception) {
                    emptyList()
                }
            }
            isLoading = false
        }
    }

    fun getPathById(id: Int): Path? {
        return paths.find { it.id == id }
    }
}
