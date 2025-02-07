package stanyliaINC.hungrymykola.activity

import stanyliaINC.hungrymykola.utils.LocaleManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.database.MealRepository
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.databinding.ActivityProductListBinding
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.Product
import stanyliaINC.hungrymykola.utils.ShoppingListProductAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ShoppingListProductAdapter
    private lateinit var productRepository: ProductRepository
    private lateinit var mealRepository: MealRepository
    private lateinit var dishRepository: DishRepository
    private lateinit var dishDao: DishDao
    private lateinit var productList: List<Pair<Product, Double>>
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = DatabaseProvider.getDatabase(applicationContext)
        dishDao = database.dishDao()
        productRepository = ProductRepository(database.productDao())
        mealRepository = MealRepository(database.mealDao(), dishDao, this)
        dishRepository = DishRepository(dishDao)

        val startDate = intent.getStringExtra("startDate") ?: ""
        val endDate = intent.getStringExtra("endDate") ?: ""

        lifecycleScope.launch {
            loadProducts(startDate, endDate)
        }

        binding.buttonMandatoryProducts.setOnClickListener {
            startActivity(Intent(this, MandatoryProductsActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        val startDate = intent.getStringExtra("startDate") ?: ""
        val endDate = intent.getStringExtra("endDate") ?: ""

        lifecycleScope.launch {
            loadProducts(startDate, endDate)
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("DefaultLocale")
    private suspend fun loadProducts(start: String, end: String) {
        productList = getProductsForDateRange(start, end)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedProductsSet = prefs.getStringSet("mandatoryProducts", emptySet()) ?: emptySet()
        val flattenedProductNamesSet = savedProductsSet.map { name -> name.split("|") }.flatten()


        val filteredProductsWithPrices = productList.filter { prod ->
            !flattenedProductNamesSet.contains(prod.first.name) && !flattenedProductNamesSet.contains(
                prod.first.nameUk
            )
        }

        adapter = ShoppingListProductAdapter(this@ProductListActivity, filteredProductsWithPrices)
        binding.productRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productRecyclerView.adapter = adapter

        binding.generalPriceContainer.text = this@ProductListActivity.getString(R.string.grn,
            String.format("%.2f", filteredProductsWithPrices.sumOf { pair -> pair.second })
        )
    }

    private suspend fun getProductsForDateRange(
        start: String,
        end: String
    ): List<Pair<Product, Double>> {

        return withContext(Dispatchers.IO) {
            val allMeals = mutableSetOf<List<Meal>>()
            val allDishes = mutableListOf<Dish>()

            getDateRangeList(start, end).forEach {
                allMeals.add(mealRepository.getMealsByDate(it))
            }

            allMeals.flatten().map { meal -> meal.dishes }.flatten().forEach {
                val dish = dishRepository.getDishByName(it)
                if (dish != null) {
                    allDishes.add(dish)
                }
            }

            val summedProductAmounts = allDishes.flatMap { dish ->
                dish.products.map { product ->
                    productRepository.getByName(product["name"]!!)
                        .first() to (product["amount"]?.toIntOrNull() ?: 0)
                }
            }.groupBy({ it.first }, { it.second })
                .mapValues { entry -> entry.value.sum() }

            summedProductAmounts.map { entry ->
                val result = entry.value.toFloat() / entry.key.amount.toFloat()
                val updatedPrice = entry.key.price * result
                entry.key to updatedPrice
            }
        }
    }

    private fun getDateRangeList(start: String, end: String): List<String> {
        val startDate = dateFormat.parse(start) ?: return emptyList()
        val endDate = dateFormat.parse(end) ?: return emptyList()

        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }
}
