package stanyliaINC.hungrymykola.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.service.PriceUpdateService


class DishDetailsActivity : AppCompatActivity() {
    private lateinit var dishName: String
    private lateinit var dishType: List<String>
    private var dishServings: Int = 0
    private lateinit var dishProducts: String
    private lateinit var dishRecipe: String
    private lateinit var dishPrice: String
    private lateinit var priceUpdateService: PriceUpdateService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleManager.setLocale(this, LocaleManager.getLanguage(this) )

        setContentView(R.layout.activity_dish_details)

        priceUpdateService = PriceUpdateService(this)
        dishName = intent.getStringExtra("DISH_NAME") ?: "Unknown Dish"
        dishType = intent.getStringArrayListExtra("DISH_TYPE") ?: emptyList()
        dishServings = intent.getIntExtra("DISH_SERVINGS", 0)
        dishProducts = intent.getStringExtra("DISH_PRODUCTS") ?: "Unknown products"
        dishRecipe = intent.getStringExtra("DISH_RECIPE") ?: "No recipe"
        dishPrice = intent.getStringExtra("DISH_PRICE") ?: "No price"

        val dishNameTextView: TextView = findViewById(R.id.dishNameTextView)
        val dishTypeTextView: TextView = findViewById(R.id.dishTypeTextView)
        val dishServingsTextView: TextView = findViewById(R.id.dishServingsTextView)
        val dishProductsTextView: TextView = findViewById(R.id.dishProductsTextView)
        val dishRecipeTextView: TextView = findViewById(R.id.dishRecipeTextView)
        val dishPriceTextView: TextView = findViewById(R.id.dishPriceTextView)

        dishNameTextView.text = dishName
        dishTypeTextView.text = dishType.joinToString(separator = this@DishDetailsActivity.getString(R.string.or))
        dishServingsTextView.text = this@DishDetailsActivity.getString(R.string.dish_servings, dishServings)
        dishProductsTextView.text = this@DishDetailsActivity.getString(R.string.products, dishProducts)
        dishRecipeTextView.text = this@DishDetailsActivity.getString(R.string.recipe, dishRecipe)
        dishPriceTextView.text = this@DishDetailsActivity.getString(R.string.possible_price, dishPrice)

        val updateDishPriceButton: Button = findViewById(R.id.updateDishPriceButton)
        updateDishPriceButton.setOnClickListener {
            lifecycleScope.launch {
                val newPrice = priceUpdateService.updateDishPrice(dishName)
                dishPriceTextView.text = this@DishDetailsActivity.getString(R.string.possible_price, newPrice.toString())
            }
        }
    }
}
