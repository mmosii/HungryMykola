package stanyliaINC.hungrymykola.database

import LocaleManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.MealDao
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.MealType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealRepository(private val mealDao: MealDao, private val dishDao: DishDao, private val context: Context) {
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    private val mealInsertMutex = Mutex()

    companion object {
        private const val TAG = "MealRepository"
    }

    suspend fun addMealsForDate(date: String) {
        mealInsertMutex.withLock {
            val allMeals = mealDao.getAllMeals()
            val allDishes = dishDao.getAllDishes()
            Log.d("", "starting addMealsForDate for $date")

            val breakfastMeal: Meal
            val breakfastDishes =
                allDishes.filter { dish -> dish.type.contains(MealType.BREAKFAST) }
            val breakfastPreviousMeals = allMeals.filter { meal -> meal.type == MealType.BREAKFAST }
            if (breakfastPreviousMeals.none { meal -> meal.date == date }) {
                val previousBreakfastDishValid =
                    isPreviousDishValid(breakfastPreviousMeals, breakfastDishes, date)
                if (previousBreakfastDishValid.second && !previousBreakfastDishValid.first.isNullOrEmpty()) {
                    breakfastMeal =
                        Meal(MealType.BREAKFAST, listOf(previousBreakfastDishValid.first!!), date)
                    val breakfastReuseDish =
                        dishDao.getDishByName(previousBreakfastDishValid.first!!)
                    if (breakfastReuseDish != null) dishDao.update(breakfastReuseDish)
                } else {
                    val sortedBreakfastDishes = breakfastDishes.sortedBy { dish ->
                        try {
                            dateFormat.parse(dish.lastUseDate)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val breakfastDish = sortedBreakfastDishes[0]
                    val oldUseBreakfastDate = breakfastDish.lastUseDate
                    breakfastDish.lastUseDate = date
                    breakfastDish.lastUseDateReserve = oldUseBreakfastDate
                    dishDao.update(breakfastDish)
                    breakfastMeal = Meal(
                        MealType.BREAKFAST,
                        listOf(breakfastDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(breakfastMeal)
            }

            val snackMeal: Meal
            val snackDishes = allDishes.filter { dish -> dish.type.contains(MealType.SNACK) }
            val snackPreviousMeals =
                allMeals.filter { meal -> meal.type == MealType.SNACK }
            if (snackPreviousMeals.none { meal -> meal.date == date }) {
                val previousSnackDishValid =
                    isPreviousDishValid(snackPreviousMeals, snackDishes, date)
                if (previousSnackDishValid.second && !previousSnackDishValid.first.isNullOrEmpty()) {
                    snackMeal = Meal(MealType.SNACK, listOf(previousSnackDishValid.first!!), date)
                    val snackReuseDish = dishDao.getDishByName(previousSnackDishValid.first!!)
                    if (snackReuseDish != null) dishDao.update(snackReuseDish)
                } else {
                    val sortedSnackDishes = snackDishes.sortedBy { dish ->
                        try {
                            dateFormat.parse(dish.lastUseDate)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val snackDish = sortedSnackDishes[0]
                    val oldUseSnackDate = snackDish.lastUseDate
                    snackDish.lastUseDate = date
                    snackDish.lastUseDateReserve = oldUseSnackDate
                    dishDao.update(snackDish)
                    snackMeal = Meal(
                        MealType.SNACK,
                        listOf(snackDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(snackMeal)
            }

            val lunchMeal: Meal
            val lunchDishes = allDishes.filter { dish -> dish.type.contains(MealType.LUNCH) }
            Log.d("", " lunchDishes" + lunchDishes)
            val lunchPreviousMeals = allMeals.filter { meal -> meal.type == MealType.LUNCH }
            if (lunchPreviousMeals.none { meal -> meal.date == date }) {
                val previousLunchDishValid =
                    isPreviousDishValid(lunchPreviousMeals, lunchDishes, date)
                if (previousLunchDishValid.second && !previousLunchDishValid.first.isNullOrEmpty()) {
                    lunchMeal = Meal(MealType.LUNCH, listOf(previousLunchDishValid.first!!), date)
                    val lunchReuseDish = dishDao.getDishByName(previousLunchDishValid.first!!)
                    if (lunchReuseDish != null) dishDao.update(lunchReuseDish)
                } else {
                    val sortedLunchDishes = lunchDishes.sortedBy { dish ->
                        try {
                            dateFormat.parse(dish.lastUseDate)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val lunchDish = sortedLunchDishes[0]
                    val oldUseLunchDate = lunchDish.lastUseDate
                    lunchDish.lastUseDate = date
                    lunchDish.lastUseDateReserve = oldUseLunchDate
                    dishDao.update(lunchDish)
                    lunchMeal = Meal(
                        MealType.LUNCH,
                        listOf(lunchDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(lunchMeal)
            }

            val dinnerMeal: Meal
            val dinnerDishes = allDishes.filter { dish -> dish.type.contains(MealType.DINNER) }
            val dinnerPreviousMeals = allMeals.filter { meal -> meal.type == MealType.DINNER }
            if (dinnerPreviousMeals.none { meal -> meal.date == date }) {
                val previousDinnerDishValid =
                    isPreviousDishValid(dinnerPreviousMeals, dinnerDishes, date)
                if (previousDinnerDishValid.second && !previousDinnerDishValid.first.isNullOrEmpty()) {
                    dinnerMeal =
                        Meal(MealType.DINNER, listOf(previousDinnerDishValid.first!!), date)
                    val dinnerReuseDish = dishDao.getDishByName(previousDinnerDishValid.first!!)
                    if (dinnerReuseDish != null) dishDao.update(dinnerReuseDish)
                } else {
                    val sortedDinnerDishes = dinnerDishes.sortedBy { dish ->
                        try {
                            dateFormat.parse(dish.lastUseDate)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    val dinnerDish = sortedDinnerDishes[0]
                    val oldUseDinnerDate = dinnerDish.lastUseDate
                    dinnerDish.lastUseDate = date
                    dinnerDish.lastUseDateReserve = oldUseDinnerDate
                    dishDao.update(dinnerDish)
                    dinnerMeal = Meal(
                        MealType.DINNER,
                        listOf(dinnerDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(dinnerMeal)
            }
            Log.d("", "finishing addMealsForDate for $date")
        }
    }

    suspend fun insertMeal(meal: Meal) {
        runCatching {
            mealDao.insert(meal)
        }.onSuccess {
            Log.d(TAG, "Successfully inserted meal: $meal")
        }.onFailure {
            Log.e(TAG, "Error inserting meal: $meal", it)
        }
    }

    suspend fun insertMeals(meals: List<Meal>) {
        runCatching {
            mealDao.insertAll(meals)
        }.onSuccess {
            Log.d(TAG, "Successfully inserted ${meals.size} meals")
        }.onFailure {
            Log.e(TAG, "Error inserting meals: $meals", it)
        }
    }

    suspend fun updateMeal(meal: Meal) {
        runCatching {
            mealDao.update(meal)
        }.onSuccess {
            Log.d(TAG, "Successfully updated meal: $meal")
        }.onFailure {
            Log.e(TAG, "Error updating meal: $meal", it)
        }
    }

    suspend fun deleteMeal(meal: Meal) {
        runCatching {
            mealDao.delete(meal)
        }.onSuccess {
            Log.d(TAG, "Successfully deleted meal: $meal")
        }.onFailure {
            Log.e(TAG, "Error deleting meal: $meal", it)
        }
    }

    suspend fun getMealByDateAndType(date: String, type: String): Meal? {
        return runCatching {
            mealDao.getMealByDateAndType(date, type)
        }.onSuccess { meal ->
            Log.d(TAG, "Fetched meal for date: $date, type: $type → $meal")
        }.onFailure {
            Log.e(TAG, "Error fetching meal for date: $date, type: $type", it)
        }.getOrNull()
    }

    suspend fun getMealsByDate(date: String): List<Meal> {
        return runCatching {
            mealDao.getMealsByDate(date)
        }.onSuccess { meals ->
            Log.d(TAG, "Fetched ${meals.size} meals for date: $date")
        }.onFailure {
            Log.e(TAG, "Error fetching meals for date: $date", it)
        }.getOrDefault(emptyList())
    }

    suspend fun getAllMeals(): List<Meal> {
        return runCatching {
            mealDao.getAllMeals()
        }.onSuccess { meals ->
            Log.d(TAG, "Fetched all meals (${meals.size})")
        }.onFailure {
            Log.e(TAG, "Error fetching all meals", it)
        }.getOrDefault(emptyList())
    }

    suspend fun deleteMealByDateAndType(date: String, type: String) {
        runCatching {
            mealDao.deleteMealByDateAndType(date, type)
        }.onSuccess {
            Log.d(TAG, "Deleted meal for date: $date and type: $type")
        }.onFailure {
            Log.e(TAG, "Error deleting meal for date: $date and type: $type", it)
        }
    }

    suspend fun deleteAllMeals() {
        runCatching {
            mealDao.deleteAllMeals()
        }.onSuccess {
            Log.d(TAG, "Successfully deleted all meals")
        }.onFailure {
            Log.e(TAG, "Error deleting all meals", it)
        }
    }

    private fun isPreviousDishValid(
        meals: List<Meal>,
        dishes: List<Dish>,
        date: String
    ): Pair<String?, Boolean> {
        if (meals.isEmpty()) {
            return Pair(null, false)
        }

        var servingsCounter = 1
        var previousDate = getPrevDate(date)

        var previousMeal =
            meals.find { meal -> meal.date == previousDate } ?: return Pair(null, false)
        val previousDishName = previousMeal.dishes.firstOrNull() ?: return Pair(null, false)

        val dish =
            dishes.find { it.dishName == previousDishName || it.dishNameUk == previousDishName }
                ?: return Pair(previousDishName, false)

        val maxServings = dish.servings

        while (true) {
            previousDate = getPrevDate(previousDate)
            previousMeal = meals.find { meal -> meal.date == previousDate } ?: break
            val nextDishName = previousMeal.dishes.firstOrNull() ?: break

            if (nextDishName == previousDishName) {
                servingsCounter++
            } else if (nextDishName == "Dummy") {
                continue
            } else {
                break
            }
        }

        return Pair(previousDishName, servingsCounter < maxServings)
    }


    private fun getPrevDate(date: String): String {
        val parsedDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance().apply {
            if (parsedDate != null) {
                time = parsedDate
            }
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val previousDate = dateFormat.format(calendar.time)
        return previousDate
    }
}
