package com.starhoop.hoopstar.domain.model

data class Player(
    val playerId: Int,
    val teamId: Int,
    val jerseyNumber: Int,
    val fullName: String,
    val photoUrl: String?,
    val birthYear: Int?
)

data class Team(
    val teamId: Int,
    val coachId: Int,
    val name: String,
    val season: String,
    val color: String?,
    val logoUrl: String?,
    val players: List<Player>
)