package stanyliaINC.hungrymykola.activity

import LocaleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.MealDao
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.MealRepository
import stanyliaINC.hungrymykola.databinding.ActivityMenuBinding
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.MealType
import stanyliaINC.hungrymykola.utils.CalendarAdapter
import stanyliaINC.hungrymykola.viewmodel.MealViewModel
import java.text.SimpleDateFormat
import java.util.*

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val calendar = Calendar.getInstance()
    private val selectedPeriod = 15
    private lateinit var dishDao: DishDao
    private lateinit var productDao: ProductDao
    private lateinit var mealDao: MealDao
    private lateinit var mealRepository: MealRepository
    private lateinit var mealViewModel: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        val database = DatabaseProvider.getDatabase(applicationContext)
        dishDao = database.dishDao()
        productDao = database.productDao()
        mealDao = database.mealDao()
        mealRepository = MealRepository(mealDao, dishDao, this@MenuActivity)

        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateCalendarView()

        binding.updateMealsButton.setOnClickListener {
            updateMealsForPeriod()
        }
    }

    private fun updateCalendarView() {
        val dates = getDatesForPeriod().sorted()
        val currentLocale = LocaleManager.getLanguage(this)?.let { Locale(it) } ?: Locale.getDefault()
        val dateFormat = SimpleDateFormat("dd.MM.yy", currentLocale)

        lifecycleScope.launch {
            binding.recyclerView.layoutManager = GridLayoutManager(this@MenuActivity, 5)

            binding.recyclerView.adapter = CalendarAdapter(dates, mealDao, this@MenuActivity, { selectedDate ->
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
            val snackMeal = mealsByDate.first { meal -> meal.type == MealType.SNACK }
            val lunchMeal = mealsByDate.first { meal -> meal.type == MealType.LUNCH }
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

            binding.tvDinnerDish.text = dinnerMeal.dishes.first()
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
            binding.updateMealsButton,
            binding.cardViewSelectedDate,
            binding.cardView,
            binding.tvSnackDish,
            binding.tvLunchDish,
            binding.tvDinnerDish,
            binding.tvBreakfastDish,
            binding.btnRemoveBreakfast,
            binding.btnRemoveDinner,
            binding.btnRemoveLunch,
            binding.btnRemoveDinner,
            binding.btnRemoveSnack
        )
        views.forEach { it.visibility = View.GONE }

        binding.inputTextField.visibility = View.VISIBLE
        binding.confirmMealButton.visibility = View.VISIBLE
        binding.cancelMealChange.visibility = View.VISIBLE

        binding.confirmMealButton.setOnClickListener{
            val newText = binding.inputTextField.text
            view.text = newText
            binding.inputTextField.visibility = View.GONE
            binding.confirmMealButton.visibility = View.GONE
            binding.cancelMealChange.visibility = View.GONE
            views.forEach { it.visibility = View.VISIBLE }
            val imm = this@MenuActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputTextField.windowToken, 0)
            lifecycleScope.launch {
                mealDao.deleteMealByDateAndType(date, type.name)
                mealDao.insert(Meal(type, listOf("Dummy", newText.toString()), date))
            }
        }
        binding.cancelMealChange.setOnClickListener{
            binding.inputTextField.visibility = View.GONE
            binding.confirmMealButton.visibility = View.GONE
            binding.cancelMealChange.visibility = View.GONE
            views.forEach { it.visibility = View.VISIBLE }
        }
    }

    private suspend fun openDishDetails(dish: String) {
        val dishByName = dishDao.getDishByName(dish)
        if (dishByName == null) {
            Toast.makeText(this@MenuActivity,
                getString(R.string.no_dish_details), Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this@MenuActivity, DishDetailsActivity::class.java)
            intent.putExtra("DISH_NAME",
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

    private fun unitMapper(unit: String) : String {
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
        Toast.makeText(this@MenuActivity, getString(R.string.updating_menu), Toast.LENGTH_SHORT).show()
        lifecycleScope.launch {
            dates.forEach { date ->
                val formattedTime = dateFormat.format(date)
                mealViewModel = MealViewModel(mealRepository)
                mealViewModel.insertMealsForDate(formattedTime)

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            updateCalendarView()
            Toast.makeText(this@MenuActivity, getString(R.string.menu_updated), Toast.LENGTH_SHORT).show()
        }, 3000)

    }
}
