package com.example.btarduino

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class OptionsStore(context: Context) {

    private val prefs =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): List<Option> {
        val raw = prefs.getString(KEY_DATA, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Option(
                    title = obj.optString("title", "Opcion"),
                    character = obj.optString("char", "")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun save(items: List<Option>) {
        val arr = JSONArray()
        items.forEach { opt ->
            arr.put(JSONObject().apply {
                put("title", opt.title)
                put("char", opt.character)
            })
        }
        prefs.edit().putString(KEY_DATA, arr.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "options_prefs"
        private const val KEY_DATA = "options_json"
    }
}
