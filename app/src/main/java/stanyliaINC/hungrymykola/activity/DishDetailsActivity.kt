package stanyliaINC.hungrymykola.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import stanyliaINC.hungrymykola.R


class DishDetailsActivity : AppCompatActivity() {
    private lateinit var dishName: String
    private lateinit var dishDescription: String
    private lateinit var dishPrice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val language = LocaleManager.getLanguage(this)
        if (language != null) {
            LocaleManager.setLocale(this@DishDetailsActivity, language)
        }
        setContentView(R.layout.activity_dish_details)

        dishName = intent.getStringExtra("DISH_NAME") ?: "Unknown Dish"
        dishDescription = intent.getStringExtra("DISH_DESCRIPTION") ?: "No description available."
        dishPrice = intent.getStringExtra("DISH_PRICE") ?: "No price available."
        val dishNameTextView: TextView = findViewById(R.id.dishNameTextView)
        val dishDescriptionTextView: TextView = findViewById(R.id.dishDescriptionTextView)
        val dishPriceTextView: TextView = findViewById(R.id.dishPriceTextView)

        dishNameTextView.text = dishName
        dishDescriptionTextView.text = dishDescription
        dishPriceTextView.text = dishPrice

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
