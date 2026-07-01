package com.starhoop.hoopstar.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_teams")
data class HiddenTeamEntity(
    @PrimaryKey val teamId: Int,
    val coachId: Int
)