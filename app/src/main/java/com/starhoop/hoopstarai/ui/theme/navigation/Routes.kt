package com.starhoop.hoopstar.ui.navigation

import android.net.Uri

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val TEAMS = "teams"
    const val ROSTER = "roster/{teamId}"
    const val UPLOAD = "upload/{teamId}"
    const val JOB_STATUS = "job/{jobId}"
    const val MAPPING = "mapping/{jobId}/{teamId}"
    const val HIGHLIGHTS = "highlights/{jobId}/{teamId}"
    const val PLAYER = "player/{url}/{name}"

    const val PLAYER_CARD = "playercard/{playerId}/{playerName}"
    fun playerCard(playerId: Int, playerName: String) =
        "playercard/$playerId/${android.net.Uri.encode(playerName)}"
    fun roster(teamId: Int) = "roster/$teamId"
    fun upload(teamId: Int) = "upload/$teamId"
    fun jobStatus(jobId: Int) = "job/$jobId"
    fun mapping(jobId: Int, teamId: Int) = "mapping/$jobId/$teamId"
    fun highlights(jobId: Int, teamId: Int) = "highlights/$jobId/$teamId"
    fun player(url: String, name: String): String {
        val encUrl = Uri.encode(url)
        val encName = Uri.encode(name)
        return "player/$encUrl/$encName"
    }
}