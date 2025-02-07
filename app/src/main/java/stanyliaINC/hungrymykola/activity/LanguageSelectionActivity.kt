package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.utils.LocaleManager

class LanguageSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        val englishButton = findViewById<Button>(R.id.englishButton)
        val ukrainianButton = findViewById<Button>(R.id.ukrainianButton)

        englishButton.setOnClickListener {
            LocaleManager.setLocale(this, "en")
            navigateToMainScreen()
        }

        ukrainianButton.setOnClickListener {
            LocaleManager.setLocale(this, "uk")
            navigateToMainScreen()
        }
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
