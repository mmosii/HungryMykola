package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val language = LocaleManager.getLanguage(this)
        if (language.isNullOrEmpty()) {
            startActivity(Intent(this, LanguageSelectionActivity::class.java))
            finish()
            return
        }
        LocaleManager.setLocale(this@MainActivity, language)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.section1.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        binding.section2.setOnClickListener {
            startActivity(Intent(this, ShoppingListActivity::class.java))
        }

        binding.section3.setOnClickListener {
            startActivity(Intent(this, AddingMealActivity::class.java))
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
