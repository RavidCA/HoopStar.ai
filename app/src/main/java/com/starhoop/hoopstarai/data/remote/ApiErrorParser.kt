package com.starhoop.hoopstarai.data.remote

import org.json.JSONArray
import org.json.JSONObject

/** מנתח שגיאות FastAPI: השדה "detail" יכול להיות String או מערך ולידציה. */
object ApiErrorParser {
    fun parse(errorBody: String?, fallback: String = "Something went wrong."): String {
        if (errorBody.isNullOrBlank()) return fallback
        return try {
            val json = JSONObject(errorBody)
            when (val detail = json.opt("detail")) {
                is String -> detail
                is JSONArray -> {
                    val msgs = buildList {
                        for (i in 0 until detail.length()) {
                            val item = detail.optJSONObject(i) ?: continue
                            item.optString("msg").takeIf { it.isNotBlank() }?.let { add(it) }
                        }
                    }
                    if (msgs.isEmpty()) fallback else msgs.joinToString("\n")
                }
                else -> fallback
            }
        } catch (e: Exception) {
            fallback
        }
    }
}