package com.example.szlaki.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.szlaki.model.Path
import com.example.szlaki.model.SavedTime

@Database(entities = [Path::class, SavedTime::class], version = 3, exportSchema = false)
abstract class PathDatabase : RoomDatabase() {
    abstract fun pathDao(): PathDao
    abstract fun savedTimeDao(): SavedTimeDao

    companion object {
        @Volatile
        private var INSTANCE: PathDatabase? = null

        fun getDatabase(context: Context): PathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PathDatabase::class.java,
                    "path_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
