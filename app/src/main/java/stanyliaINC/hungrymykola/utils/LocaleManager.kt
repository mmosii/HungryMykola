import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LocaleManager {
    private const val LANGUAGE_KEY = "language_key"

    fun setLocale(context: Context, language: String) {
        saveLanguage(context, language)
        updateResources(context, language)
    }

    fun getLanguage(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(LANGUAGE_KEY, null)
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(LANGUAGE_KEY, language).apply()
    }

    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
}
