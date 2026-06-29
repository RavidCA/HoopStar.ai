package com.starhoop.hoopstar.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerDto(
    @Json(name = "player_id") val playerId: Int,
    @Json(name = "team_id") val teamId: Int,
    @Json(name = "jersey_number") val jerseyNumber: Int,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "photo_url") val photoUrl: String?,
    @Json(name = "birth_year") val birthYear: Int?
)

@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "team_id") val teamId: Int,
    @Json(name = "coach_id") val coachId: Int,
    val name: String,
    val season: String?,
    val color: String?,
    @Json(name = "logo_url") val logoUrl: String?,
    val players: List<PlayerDto>?
)

@JsonClass(generateAdapter = true)
data class CreateTeamRequest(
    val name: String,
    val season: String? = null,
    val color: String? = null,
    @Json(name = "logo_url") val logoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class CreatePlayerRequest(
    @Json(name = "jersey_number") val jerseyNumber: Int,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "photo_url") val photoUrl: String? = null,
    @Json(name = "birth_year") val birthYear: Int? = null
)

/** PUT מקבל subset — שדות null לא נשלחים (Moshi משמיט nulls). */
@JsonClass(generateAdapter = true)
data class UpdatePlayerRequest(
    @Json(name = "jersey_number") val jerseyNumber: Int? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "photo_url") val photoUrl: String? = null,
    @Json(name = "birth_year") val birthYear: Int? = null
)