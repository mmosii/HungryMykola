package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import stanyliaINC.hungrymykola.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val language = LocaleManager.getLanguage(this)
        if (language != null) {
            LocaleManager.setLocale(this@SettingsActivity, language)
        }
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languages = listOf("English", "Українська")
        val languageCodes = listOf("en", "uk")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        val currentLanguage = LocaleManager.getLanguage(this)
        val currentLanguageIndex = languageCodes.indexOf(currentLanguage)
        binding.languageSpinner.setSelection(currentLanguageIndex)

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = languageCodes[position]
                if (selectedLanguageCode != LocaleManager.getLanguage(this@SettingsActivity)) {
                    LocaleManager.setLocale(this@SettingsActivity, selectedLanguageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }
    }
}
