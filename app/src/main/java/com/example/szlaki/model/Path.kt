package com.example.szlaki.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "paths")
class Path(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val parkCode: String,
    val states: String,
    var savedTimeMillis: Long = 0L,
    var savedIsTimerRunning: Boolean = false
) {
    @get:Ignore
    var timeMillis by mutableLongStateOf(savedTimeMillis)
    
    @get:Ignore
    var isTimerRunning by mutableStateOf(savedIsTimerRunning)

    // Synchronizacja stanu UI z bazą
    fun syncToPersisted() {
        savedTimeMillis = timeMillis
        savedIsTimerRunning = isTimerRunning
    }
}
