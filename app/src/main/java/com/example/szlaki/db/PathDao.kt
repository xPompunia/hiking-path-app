package com.example.szlaki.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.szlaki.model.Path
import kotlinx.coroutines.flow.Flow

@Dao
interface PathDao {
    @Query("SELECT * FROM paths")
    fun getAllPaths(): Flow<List<Path>>

    @Upsert
    suspend fun upsertAll(paths: List<Path>)

    @Query("DELETE FROM paths")
    suspend fun deleteAll()
}
