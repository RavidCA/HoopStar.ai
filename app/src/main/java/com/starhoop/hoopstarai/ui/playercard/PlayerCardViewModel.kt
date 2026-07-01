package com.starhoop.hoopstar.ui.playercard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starhoop.hoopstar.data.local.db.SavedReelEntity
import com.starhoop.hoopstar.data.repository.SavedReelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerCardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val savedReels: SavedReelsRepository
) : ViewModel() {

    val playerId: Int = savedStateHandle.get<Int>("playerId") ?: -1
    val playerName: String = savedStateHandle.get<String>("playerName") ?: "Player"

    val reels: StateFlow<List<SavedReelEntity>> =
        savedReels.reelsForPlayer(playerId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteReel(id: Long) {
        viewModelScope.launch { savedReels.delete(id) }
    }
}