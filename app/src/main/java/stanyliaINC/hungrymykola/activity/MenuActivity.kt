package stanyliaINC.hungrymykola.activity

import stanyliaINC.hungrymykola.utils.LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.MealDao
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.database.MealRepository
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.databinding.ActivityMenuBinding
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.MealType
import stanyliaINC.hungrymykola.model.Product
import stanyliaINC.hungrymykola.utils.MenuCalendarAdapter
import stanyliaINC.hungrymykola.viewmodel.MealViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.net.ssl.SSLHandshakeException

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val calendar = Calendar.getInstance()
    private val selectedPeriod = 15
    private lateinit var dishDao: DishDao
    private lateinit var productDao: ProductDao
    private lateinit var mealDao: MealDao
    private lateinit var mealRepository: MealRepository
    private lateinit var mealViewModel: MealViewModel
    private lateinit var productRepository: ProductRepository
    private lateinit var dishRepository: DishRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        val database = DatabaseProvider.getDatabase(applicationContext)
        dishDao = database.dishDao()
        productDao = database.productDao()
        mealDao = database.mealDao()
        dishRepository = DishRepository(dishDao)
        mealRepository = MealRepository(mealDao, dishDao, this@MenuActivity)
        productRepository = ProductRepository(productDao)

        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateMealsForPeriod()
        updateCalendarView()
    }

    private fun updateCalendarView() {
        val dates = getDatesForPeriod().sorted()
        val currentLocale =
            LocaleManager.getLanguage(this)?.let { Locale(it) } ?: Locale.getDefault()
        val dateFormat = SimpleDateFormat("dd.MM.yy", currentLocale)

        lifecycleScope.launch {
            binding.recyclerView.layoutManager = GridLayoutManager(this@MenuActivity, 5)

            binding.recyclerView.adapter =
                MenuCalendarAdapter(dates, mealDao, this@MenuActivity, { selectedDate ->
                    lifecycleScope.launch {
                        showMenuForDay(dateFormat.format(selectedDate), mealDao)
                    }
                }, this@MenuActivity)
        }
    }

    private suspend fun showMenuForDay(date: String, mealDao: MealDao) {
        val mealsByDate = mealDao.getMealsByDate(date)
        if (mealsByDate.isEmpty()) {
            Toast.makeText(this@MenuActivity, R.string.no_menu, Toast.LENGTH_SHORT).show()
        } else {
            val breakfastMeal = mealsByDate.first { meal -> meal.type == MealType.BREAKFAST }
            val sandwichMeal = mealsByDate.first { meal -> meal.type == MealType.SANDWICH }
            val lunchMeal = mealsByDate.first { meal -> meal.type == MealType.LUNCH }
            val snackMeal = mealsByDate.first { meal -> meal.type == MealType.SNACK }
            val dinnerMeal = mealsByDate.first { meal -> meal.type == MealType.DINNER }

            if (breakfastMeal.dishes.first() == "Dummy") {
                binding.tvBreakfastDish.text = breakfastMeal.dishes[1]
            } else {
                binding.tvBreakfastDish.text = breakfastMeal.dishes.first()
            }

            binding.tvBreakfastDish.setOnClickListener {
                lifecycleScope.launch {
                    openDishDetails(binding.tvBreakfastDish.text.toString())
                }
            }

            if (sandwichMeal.dishes.first() == "Dummy") {
                binding.tvSandwichDish.text = sandwichMeal.dishes[1]
            } else {
                binding.tvSandwichDish.text = sandwichMeal.dishes.first()
            }

            binding.tvSandwichDish.setOnClickListener {
                lifecycleScope.launch {
                    openDishDetails(binding.tvSandwichDish.text.toString())
                }
            }

            if (snackMeal.dishes.first() == "Dummy") {
                binding.tvSnackDish.text = snackMeal.dishes[1]
            } else {
                binding.tvSnackDish.text = snackMeal.dishes.first()
            }

            binding.tvSnackDish.setOnClickListener {
                lifecycleScope.launch {
                    openDishDetails(binding.tvSnackDish.text.toString())
                }
            }

            if (lunchMeal.dishes.first() == "Dummy") {
                binding.tvLunchDish.text = lunchMeal.dishes[1]
            } else {
                binding.tvLunchDish.text = lunchMeal.dishes.first()
            }

            binding.tvLunchDish.setOnClickListener {
                lifecycleScope.launch {
                    openDishDetails(binding.tvLunchDish.text.toString())
                }
            }

            if (dinnerMeal.dishes.first() == "Dummy") {
                binding.tvDinnerDish.text = dinnerMeal.dishes[1]
            } else {
                binding.tvDinnerDish.text = dinnerMeal.dishes.first()
            }
            binding.tvDinnerDish.setOnClickListener {
                lifecycleScope.launch {
                    openDishDetails(binding.tvDinnerDish.text.toString())
                }
            }
            binding.selectedDate.text = date

            binding.btnRemoveBreakfast.setOnClickListener {
                lifecycleScope.launch {
                    replaceMeal(date, MealType.BREAKFAST, binding.tvBreakfastDish)
                }
            }

            binding.btnRemoveSandwich.setOnClickListener {
                lifecycleScope.launch {
                    replaceMeal(date, MealType.SANDWICH, binding.tvSandwichDish)
                }
            }

            binding.btnRemoveSnack.setOnClickListener {
                lifecycleScope.launch {
                    replaceMeal(date, MealType.SNACK, binding.tvSnackDish)
                }
            }

            binding.btnRemoveLunch.setOnClickListener {
                lifecycleScope.launch {
                    replaceMeal(date, MealType.LUNCH, binding.tvLunchDish)
                }
            }

            binding.btnRemoveDinner.setOnClickListener {
                lifecycleScope.launch {
                    replaceMeal(date, MealType.DINNER, binding.tvDinnerDish)
                }
            }
        }
    }

    private fun replaceMeal(date: String, type: MealType, view: TextView) {
        val views = listOf(
            binding.tvBreakfastDish,
            binding.tvSnackDish,
            binding.tvLunchDish,
            binding.tvDinnerDish,
            binding.selectedDate,
            binding.cardViewSelectedDate,
            binding.cardView,
            binding.btnRemoveBreakfast,
            binding.btnRemoveLunch,
            binding.btnRemoveDinner,
            binding.btnRemoveSnack,
            binding.tvSandwichDish,
            binding.btnRemoveSandwich
        )
        views.forEach { it.visibility = View.GONE }

        binding.inputTextField.visibility = View.VISIBLE
        binding.confirmMealButton.visibility = View.VISIBLE
        binding.cancelMealChange.visibility = View.VISIBLE

        lifecycleScope.launch {
            val allDishes = dishRepository.getAllDishes()
            val dishNames = allDishes.map { it.dishName } + allDishes.map { it.dishNameUk }
            val adapter = ArrayAdapter(
                this@MenuActivity,
                android.R.layout.simple_dropdown_item_1line,
                dishNames
            )

            binding.inputTextField.setAdapter(adapter)
        }

        binding.confirmMealButton.setOnClickListener {
            val newText = binding.inputTextField.text

            if (newText.isNullOrEmpty()) {
                Toast.makeText(
                    this@MenuActivity,
                    getString(R.string.please_enter_a_valid_dish_name),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            view.text = newText

            val imm =
                this@MenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputTextField.windowToken, 0)

            lifecycleScope.launch {
                val allMealsFromDate = mealRepository.getMealsFromDate(date)
                    .filter { it.type == type }
                val allDishesInAffectedMeals = allMealsFromDate.flatMap { it.dishes }.toSet()
                allDishesInAffectedMeals.forEach { dishName ->
                    val dish = dishRepository.getDishByName(dishName)
                    if (dish != null) {
                        val updatedUseDates = dish.useDates.toMutableList()
                        val index = updatedUseDates.indexOfFirst { it >= date }
                        if (index != -1) {
                            updatedUseDates.subList(index, updatedUseDates.size)
                                .clear()

                            dish.useDates = updatedUseDates
                            dishRepository.insert(dish)
                        }
                    }
                }

                allMealsFromDate.forEach { mealRepository.deleteMeal(it) }

                val fetchedDish = dishRepository.getDishByName(newText.toString())
                if (fetchedDish != null) {
                    val newUseDates = fetchedDish.useDates.toMutableList()
                    newUseDates.add(date)
                    fetchedDish.useDates = newUseDates
                    dishRepository.insert(fetchedDish)

                    mealRepository.insertMeal(Meal(type, listOf(newText.toString()), date))
                } else {
                    mealRepository.insertMeal(Meal(type, listOf("Dummy", newText.toString()), date))
                }
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        binding.cancelMealChange.setOnClickListener {
            binding.inputTextField.visibility = View.GONE
            binding.confirmMealButton.visibility = View.GONE
            binding.cancelMealChange.visibility = View.GONE
            views.forEach { it.visibility = View.VISIBLE }
        }
    }

    private suspend fun openDishDetails(dish: String) {
        val dishByName = dishDao.getDishByName(dish)
        if (dishByName == null) {
            Toast.makeText(
                this@MenuActivity,
                getString(R.string.no_dish_details), Toast.LENGTH_SHORT
            ).show()
        } else {
            val intent = Intent(this@MenuActivity, DishDetailsActivity::class.java)
            intent.putExtra(
                "DISH_NAME",
                dishByName.getLocalizedDishName(LocaleManager.getLanguage(this@MenuActivity))
            )
            intent.putExtra("DISH_SERVINGS", dishByName.servings)
            intent.putStringArrayListExtra(
                "DISH_TYPE",
                ArrayList(dishByName.type.map { it.getLocalizedDishName(this@MenuActivity) })
            )
            val localizedDishProducts = dishByName.products.map { productMap ->
                val newMap = mutableMapOf<String, Any>()

                for ((key, value) in productMap) {
                    when (key) {
                        "name" -> {
                            val localizedProductName =
                                productDao.getByName(value).firstOrNull()?.getLocalizedProduct(
                                    LocaleManager.getLanguage(this@MenuActivity)
                                ) ?: value
                            newMap[key] = localizedProductName
                        }

                        "unit" -> {
                            val mappedUnit = unitMapper(value)
                            newMap[key] = mappedUnit
                        }

                        else -> newMap[key] = value
                    }
                }

                newMap
            }
            val dishProductsString =
                localizedDishProducts.joinToString(separator = "\n") { "${it["name"]} -  ${it["amount"]} ${it["unit"]}" }
            intent.putExtra("DISH_PRODUCTS", dishProductsString)
            intent.putExtra("DISH_RECIPE", dishByName.recipe)
            intent.putExtra("DISH_PRICE", dishByName.price.toString())
            startActivity(intent)
        }
    }

    private fun unitMapper(unit: String): String {
        when (unit) {
            "g", "G", "г", "Г" -> return this@MenuActivity.getString(R.string.unit_g)
            "ml", "ML", "мл", "МЛ" -> return this@MenuActivity.getString(R.string.unit_ml)
            "PCS", "pcs", "шт", "ШТ" -> return this@MenuActivity.getString(R.string.unit_pcs)
        }
        return "units"
    }

    private fun getDatesForPeriod(): List<Date> {
        val dates = mutableListOf<Date>()
        val tempCalendar = calendar.clone() as Calendar

        for (i in 0 until selectedPeriod) {
            dates.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }

    private fun updateMealsForPeriod() {
        val dates = getDatesForPeriod().sorted()
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        lifecycleScope.launch {
            dates.forEach { date ->
                val formattedTime = dateFormat.format(date)
                mealViewModel = MealViewModel(mealRepository)
                mealViewModel.insertMealsForDate(formattedTime)
            }
        }
        lifecycleScope.launch {
            val allProducts = productRepository.getAllProducts()
            updatePriceForProducts(allProducts)
        }
    }

    private fun updatePriceForProducts(products: List<Product>) {
        products.filter { prod ->
            !prod.url.isNullOrEmpty() && LocalDate.parse(prod.priceUpdateDate).isBefore(
                LocalDate.now().minusDays(7)
            )
        }.forEach {
            if (it.url!!.contains("rukavychka")) {
                CoroutineScope(Dispatchers.IO).launch {
                    val client = OkHttpClient()
                    val request = Request.Builder()
                        .url(it.url!!)
                        .addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
                        )
                        .build()

                    try {
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            val html = response.body?.string()
                            val doc = Jsoup.parse(html!!)

                            val priceText = doc.select("span.fm-module-price-new").text()
                            val result = priceText.replace(Regex("[^0-9.]"), "").removeSuffix(".")
                            Log.d("Price Extracted", "Price: ${it.nameUk} $result")

                            it.price = if (it.amount == 1000) result.toDouble() * 10 else result.toDouble()
                            it.priceUpdateDate = LocalDate.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            productRepository.update(it)
                        } else {
                            Log.d(
                                "Price update error",
                                "Error while updating price for " + it.toString()
                            )
                        }
                    } catch (e: SSLHandshakeException) {
                        Log.e("SSL Error", "SSL handshake failed for ${it.nameUk}: ${e.message}")
                    } catch (e: Exception) {
                        Log.e(
                            "Network Error",
                            "Error while updating price for ${it.nameUk}: ${e.message}"
                        )
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(it.url!!)
                            .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
                            )
                            .build()

                        val response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            val html = response.body?.string()
                            val doc = Jsoup.parse(html!!)
                            val price = doc.select("meta[property=og:title]").attr("content")
                                .takeIf { it.contains("ціною від") }?.split("від")?.last()?.trim()
                            val result = price?.replace(Regex("[^0-9.]"), "")

                            Log.d("Price Extracted", "Price: ${it.nameUk} $result")

                            if (result != null) {
                                it.price = result.toDouble()
                                it.priceUpdateDate = LocalDate.now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                productRepository.update(it)
                                Log.d(
                                    "Price Update",
                                    "Price updated successfully for ${it.nameUk}: $result"
                                )
                            } else {
                                Log.d("Price Update", "Price extraction failed for ${it.nameUk}.")
                            }
                        } else {
                            Log.d(
                                "Price update error",
                                "Error while updating price for ${it.nameUk}: ${response.message}"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "Network Error",
                            "Error while updating price for ${it.nameUk}: ${e.message}"
                        )
                    }
                }
            }

        }
    }
}
