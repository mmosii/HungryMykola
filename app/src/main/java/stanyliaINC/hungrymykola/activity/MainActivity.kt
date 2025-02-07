package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.database.MealRepository
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.databinding.ActivityMainBinding
import stanyliaINC.hungrymykola.utils.LocaleManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val productDao = DatabaseProvider.getDatabase(applicationContext).productDao()
        val dishDao = DatabaseProvider.getDatabase(applicationContext).dishDao()
        val mealDao = DatabaseProvider.getDatabase(applicationContext).mealDao()
        val productRepository = ProductRepository(productDao)
        val dishRepository = DishRepository(dishDao)
        val mealRepository = MealRepository(mealDao, dishDao, this)

        productRepository.syncAllProductsFromFirebase()
        dishRepository.syncAllDishesFromFirebase()
        mealRepository.syncRecentMealsFromFirebase()
        productRepository.syncMandatoryProductsFromFirebase(this)

        productRepository.listenForProductUpdates()
        dishRepository.listenForDishUpdates()
        mealRepository.listenForMealUpdates()
        productRepository.listenForMandatoryProductUpdates(this)

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
            startActivity(Intent(this, AddingDishActivity::class.java))
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
