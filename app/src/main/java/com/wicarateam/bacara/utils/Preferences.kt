package com.wicarateam.bacara.utils

import android.content.Context
import android.content.SharedPreferences
import com.wicarateam.bacara.helper.Constant.MEETING_PREF

class Preferences(val context: Context) {
    private val sharedPref = context.getSharedPreferences(MEETING_PREF, 0)

    fun setValues(key: String, value: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValues(key: String): String? {
        return sharedPref.getString(key, "")
    }
}