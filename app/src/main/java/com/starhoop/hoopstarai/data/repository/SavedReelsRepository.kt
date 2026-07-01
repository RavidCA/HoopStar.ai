package com.starhoop.hoopstar.data.repository

import com.starhoop.hoopstar.data.local.db.HoopStarDao
import com.starhoop.hoopstar.data.local.db.SavedReelEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedReelsRepository @Inject constructor(
    private val dao: HoopStarDao
) {
    fun reelsForPlayer(playerId: Int): Flow<List<SavedReelEntity>> = dao.reelsForPlayer(playerId)

    suspend fun save(reel: SavedReelEntity) = dao.saveReel(reel)

    suspend fun delete(id: Long) = dao.deleteReel(id)
}