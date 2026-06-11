package com.example.szlaki.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.szlaki.model.SavedTime
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTimeDao {
    @Query("SELECT * FROM saved_times WHERE pathId = :pathId ORDER BY timeMillis ASC")
    fun getTimesForPath(pathId: Int): Flow<List<SavedTime>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savedTime: SavedTime)

    @Delete
    suspend fun delete(savedTime: SavedTime)

    @Query("DELETE FROM saved_times WHERE pathId = :pathId")
    suspend fun deleteAllForPath(pathId: Int)
}
