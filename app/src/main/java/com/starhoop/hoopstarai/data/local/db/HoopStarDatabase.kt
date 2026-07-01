package com.starhoop.hoopstar.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HiddenTeamEntity::class, SavedReelEntity::class, HiddenJobEntity::class],
    version = 2,
    exportSchema = false
)
abstract class HoopStarDatabase : RoomDatabase() {
    abstract fun dao(): HoopStarDao
}