package com.nigareshat.app.utils

import android.content.Context
import android.content.SharedPreferences

class SharePreference private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "SharePre"

        @Volatile
        private var instance: SharePreference? = null

        fun getInstance(context: Context): SharePreference {
            return instance ?: synchronized(this) {
                instance ?: SharePreference(context).also { instance = it }
            }
        }
    }


    var accessToken: String?
        get() = sharedPreferences.getString("accessToken", null)
        set(value) = sharedPreferences.edit().putString("accessToken", value).apply()


    var name: String?
        get() = sharedPreferences.getString("name", null)
        set(value) = sharedPreferences.edit().putString("name", value).apply()


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}
