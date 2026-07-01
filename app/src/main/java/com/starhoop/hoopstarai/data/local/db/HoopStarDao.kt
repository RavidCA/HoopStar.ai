package com.starhoop.hoopstar.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoopStarDao {
    // --- קבוצות מוסתרות ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun hideTeam(team: HiddenTeamEntity)

    @Query("DELETE FROM hidden_teams WHERE teamId = :teamId")
    suspend fun unhideTeam(teamId: Int)

    @Query("SELECT teamId FROM hidden_teams WHERE coachId = :coachId")
    fun hiddenTeamIds(coachId: Int): Flow<List<Int>>

    // --- משחקים (jobs) מוסתרים ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun hideJob(job: HiddenJobEntity)

    @Query("SELECT jobId FROM hidden_jobs")
    suspend fun hiddenJobIds(): List<Int>

    // --- רילים שמורים ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReel(reel: SavedReelEntity)

    @Query("SELECT * FROM saved_reels WHERE playerId = :playerId ORDER BY createdAt DESC")
    fun reelsForPlayer(playerId: Int): Flow<List<SavedReelEntity>>

    @Query("DELETE FROM saved_reels WHERE id = :id")
    suspend fun deleteReel(id: Long)
}