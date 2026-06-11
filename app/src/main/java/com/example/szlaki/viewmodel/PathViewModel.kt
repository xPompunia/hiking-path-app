package com.example.szlaki.viewmodel

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.szlaki.api.NpsApiService
import com.example.szlaki.db.PathDatabase
import com.example.szlaki.model.Path
import com.example.szlaki.model.SavedTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PathViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = NpsApiService.create()
    private val database = PathDatabase.getDatabase(application)
    private val pathDao = database.pathDao()
    private val savedTimeDao = database.savedTimeDao()

    private var _paths by mutableStateOf<List<Path>>(emptyList())
    private val timerStartTimes = mutableMapOf<Int, Long>()
    
    var selectedState by mutableStateOf("Wszystkie")
    var searchQuery by mutableStateOf("")

    val isAnyTimerRunning by derivedStateOf { _paths.any { it.isTimerRunning } }
    val runningPathId by derivedStateOf { _paths.find { it.isTimerRunning }?.id }
    
    val filteredPaths by derivedStateOf {
        val filteredByState = if (selectedState == "Wszystkie") {
            _paths
        } else {
            _paths.filter { it.states.contains(selectedState) }
        }
        
        if (searchQuery.isBlank()) {
            filteredByState
        } else {
            filteredByState.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    val availableStates = listOf("Wszystkie", "WY", "CA", "UT", "WA")
    var isLoading by mutableStateOf(true)
        private set

    init {
        loadPaths()
        startGlobalTimer()
    }

    private fun loadPaths() {
        viewModelScope.launch {
            isLoading = true
            val minDisplayTime = 2000L
            val startTime = System.currentTimeMillis()
            
            val cachedPaths = pathDao.getAllPaths().first()
            if (cachedPaths.isNotEmpty()) _paths = cachedPaths

            try {
                val response = apiService.getParks()
                val apiPaths = response.data.map { parkData ->
                    val id = parkData.id.hashCode()
                    val existingPath = _paths.find { it.id == id }
                    existingPath ?: Path(
                        id = id,
                        name = parkData.fullName,
                        description = parkData.description,
                        imageUrl = parkData.images.firstOrNull()?.url ?: "",
                        parkCode = parkData.parkCode,
                        states = parkData.states
                    )
                }
                apiPaths.forEach { it.syncToPersisted() }
                pathDao.upsertAll(apiPaths)
                _paths = apiPaths
            } catch (e: Exception) {
                if (_paths.isEmpty()) _paths = emptyList()
            } finally {
                val remainingTime = minDisplayTime - (System.currentTimeMillis() - startTime)
                if (remainingTime > 0) delay(remainingTime)
                isLoading = false
            }
        }
    }

    private fun startGlobalTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!isAnyTimerRunning) continue
                val now = System.currentTimeMillis()
                _paths.forEach { path ->
                    if (path.isTimerRunning) {
                        path.timeMillis = now - (timerStartTimes[path.id] ?: now)
                    }
                }
            }
        }
    }

    fun toggleTimer(path: Path) {
        if (path.isTimerRunning) {
            path.isTimerRunning = false
            timerStartTimes.remove(path.id)
        } else {
            timerStartTimes[path.id] = System.currentTimeMillis() - path.timeMillis
            path.isTimerRunning = true
        }
        savePathState(path)
    }

    fun resetTimer(path: Path) {
        path.isTimerRunning = false
        path.timeMillis = 0L
        savePathState(path)
    }

    fun saveCurrentTime(pathId: Int, timeMillis: Long) {
        if (timeMillis > 0) {
            viewModelScope.launch {
                savedTimeDao.insert(SavedTime(pathId = pathId, timeMillis = timeMillis))
            }
        }
    }

    fun deleteSavedTime(savedTime: SavedTime) = viewModelScope.launch { savedTimeDao.delete(savedTime) }

    fun getSavedTimesForPath(pathId: Int): Flow<List<SavedTime>> = savedTimeDao.getTimesForPath(pathId)

    private fun savePathState(path: Path) = viewModelScope.launch {
        path.syncToPersisted()
        pathDao.upsertAll(listOf(path))
    }

    fun getPathById(id: Int) = _paths.find { it.id == id }

    fun loadPathDescription(path: Path, onDescriptionLoaded: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.getThingsToDo(parkCode = path.parkCode)
                val description = response.data.firstOrNull()?.longDescription ?: path.description
                onDescriptionLoaded(description)
            } catch (e: Exception) {
                onDescriptionLoaded(path.description)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _paths.forEach { it.syncToPersisted() }
    }
}
