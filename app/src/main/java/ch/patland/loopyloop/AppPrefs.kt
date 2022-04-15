package ch.patland.loopyloop

import android.content.Context

class AppPrefs() {
    private val SETTINGS = "settings"
    private val MUTE_CHECKBOX = "mute_checkbox"

    fun getMuteChecked(context: Context): Boolean {
        val settings = context.getSharedPreferences(SETTINGS, 0)
        return settings.getBoolean(MUTE_CHECKBOX, true)
    }

    fun setMuteChecked(context: Context, isMuteChecked: Boolean) {
        val settings = context.getSharedPreferences(SETTINGS, 0)
        val editor = settings.edit()
        editor.putBoolean(MUTE_CHECKBOX, isMuteChecked)
        editor.apply()
    }
}