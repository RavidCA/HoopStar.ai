package com.starhoop.hoopstar.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_reels")
data class SavedReelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerId: Int,
    val playerName: String,
    val teamId: Int,
    val jobId: Int,
    val reelId: Int,
    val composedReelId: Int?,
    val downloadUrl: String,
    val clipCount: Int,
    val durationSec: Double?,
    val createdAt: Long = System.currentTimeMillis()
)