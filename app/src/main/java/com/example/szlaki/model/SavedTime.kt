package com.example.szlaki.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_times",
    foreignKeys = [
        ForeignKey(
            entity = Path::class,
            parentColumns = ["id"],
            childColumns = ["pathId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SavedTime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pathId: Int,
    val timeMillis: Long,
    val timestamp: Long = System.currentTimeMillis()
)
