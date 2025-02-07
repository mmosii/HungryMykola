package stanyliaINC.hungrymykola.database

import stanyliaINC.hungrymykola.utils.LocaleManager
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.MealDao
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.MealType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealRepository(
    private val mealDao: MealDao, private val dishDao: DishDao, private val context: Context
) {
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
    private val firebaseDb =
        FirebaseDatabase.getInstance("")
            .getReference("meals")

    private val mealInsertMutex = Mutex()
    private val dishRepository = DishRepository(dishDao)

    companion object {
        private const val TAG = "MealRepository"
    }

    suspend fun addMealsForDate(date: String) {
        mealInsertMutex.withLock {
            val allMeals = mealDao.getAllMeals()
            val allDishes = dishDao.getAllDishes()

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
                        dishRepository.getDishByName(previousBreakfastDishValid.first!!)
                    if (breakfastReuseDish != null) dishRepository.updateDish(breakfastReuseDish)
                } else {
                    val yesterdayMeal =
                        breakfastPreviousMeals.find { meal -> meal.date == getPrevDate(date) }
                    val yesterdayDishName = yesterdayMeal?.dishes?.firstOrNull()
                    val breakfastDish = getWeightedRandomDish(
                        breakfastDishes, yesterdayDishName, breakfastPreviousMeals
                    )
                    val useDates = breakfastDish.useDates.toMutableList()
                    useDates.add(date)
                    breakfastDish.useDates = useDates

                    dishRepository.updateDish(breakfastDish)
                    breakfastMeal = Meal(
                        MealType.BREAKFAST,
                        listOf(breakfastDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(breakfastMeal)
            }

            val sandwichMeal: Meal
            val sandwichDishes = allDishes.filter { dish -> dish.type.contains(MealType.SANDWICH) }
            val sandwichPreviousMeals = allMeals.filter { meal -> meal.type == MealType.SANDWICH }
            if (sandwichPreviousMeals.none { meal -> meal.date == date }) {
                val previousSandwichDishValid =
                    isPreviousDishValid(sandwichPreviousMeals, sandwichDishes, date)
                if (previousSandwichDishValid.second && !previousSandwichDishValid.first.isNullOrEmpty()) {
                    sandwichMeal =
                        Meal(MealType.SANDWICH, listOf(previousSandwichDishValid.first!!), date)
                    val sandwichReuseDish =
                        dishRepository.getDishByName(previousSandwichDishValid.first!!)
                    if (sandwichReuseDish != null) dishRepository.updateDish(sandwichReuseDish)
                } else {
                    val sortedSandwichDishes =
                        getSortedDishesByEarliestUse(sandwichDishes, dateFormat)
                    val sandwichDish = sortedSandwichDishes[0]
                    val useDates = sandwichDish.useDates.toMutableList()
                    useDates.add(date)
                    sandwichDish.useDates = useDates
                    dishRepository.updateDish(sandwichDish)
                    sandwichMeal = Meal(
                        MealType.SANDWICH,
                        listOf(sandwichDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(sandwichMeal)
            }

            val snackMeal: Meal
            val snackDishes = allDishes.filter { dish -> dish.type.contains(MealType.SNACK) }
            val snackPreviousMeals = allMeals.filter { meal -> meal.type == MealType.SNACK }
            if (snackPreviousMeals.none { meal -> meal.date == date }) {
                val previousSnackDishValid =
                    isPreviousDishValid(snackPreviousMeals, snackDishes, date)
                if (previousSnackDishValid.second && !previousSnackDishValid.first.isNullOrEmpty()) {
                    snackMeal = Meal(MealType.SNACK, listOf(previousSnackDishValid.first!!), date)
                    val snackReuseDish =
                        dishRepository.getDishByName(previousSnackDishValid.first!!)
                    if (snackReuseDish != null) dishRepository.updateDish(snackReuseDish)
                } else {
                    val sortedSnackDishes = getSortedDishesByEarliestUse(snackDishes, dateFormat)
                    val snackDish = sortedSnackDishes[0]
                    val useDates = snackDish.useDates.toMutableList()
                    useDates.add(date)
                    snackDish.useDates = useDates
                    dishRepository.updateDish(snackDish)
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
            val lunchPreviousMeals = allMeals.filter { meal -> meal.type == MealType.LUNCH }
            if (lunchPreviousMeals.none { meal -> meal.date == date }) {
                val previousLunchDishValid =
                    isPreviousDishValid(lunchPreviousMeals, lunchDishes, date)
                if (previousLunchDishValid.second && !previousLunchDishValid.first.isNullOrEmpty()) {
                    lunchMeal = Meal(MealType.LUNCH, listOf(previousLunchDishValid.first!!), date)
                    val lunchReuseDish =
                        dishRepository.getDishByName(previousLunchDishValid.first!!)
                    if (lunchReuseDish != null) dishRepository.updateDish(lunchReuseDish)
                } else {
                    val sortedLunchDishes = getSortedDishesByEarliestUse(lunchDishes, dateFormat)
                    val lunchDish = sortedLunchDishes[0]
                    val useDates = lunchDish.useDates.toMutableList()
                    useDates.add(date)
                    lunchDish.useDates = useDates
                    dishRepository.updateDish(lunchDish)
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
                    val dinnerReuseDish =
                        dishRepository.getDishByName(previousDinnerDishValid.first!!)
                    if (dinnerReuseDish != null) dishRepository.updateDish(dinnerReuseDish)
                } else {
                    val sortedDinnerDishes = getSortedDishesByEarliestUse(dinnerDishes, dateFormat)
                    val dinnerDish = sortedDinnerDishes[0]
                    val useDates = dinnerDish.useDates.toMutableList()
                    useDates.add(date)
                    dinnerDish.useDates = useDates
                    dishRepository.updateDish(dinnerDish)
                    dinnerMeal = Meal(
                        MealType.DINNER,
                        listOf(dinnerDish.getLocalizedDishName(LocaleManager.getLanguage(context))),
                        date
                    )
                }
                insertMeal(dinnerMeal)
            }
        }
    }

    suspend fun insertMeal(meal: Meal) {
        runCatching {
            mealDao.insert(meal)
            uploadMealsToFirebase(listOf(meal))
        }.onSuccess {
            Log.d(TAG, "Successfully inserted meal: $meal")
        }.onFailure {
            Log.e(TAG, "Error inserting meal: $meal", it)
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

    private fun isPreviousDishValid(
        meals: List<Meal>, dishes: List<Dish>, date: String
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

    private fun getWeightedRandomDish(
        dishes: List<Dish>, previousDishName: String?, allPreviousMeals: List<Meal>
    ): Dish {
        val targetSpecialPercentage = 30.0

        val sortedDishes = getSortedDishesByEarliestUse(dishes, dateFormat)
        return if (previousDishName != null && previousDishName != "Boiled eggs" && previousDishName != "Варені яйця" && getIndividualSpecialDishUsage(
                allPreviousMeals, "Варені яйця"
            ) < targetSpecialPercentage && getIndividualSpecialDishUsage(
                allPreviousMeals, "Boiled eggs"
            ) < targetSpecialPercentage
        ) {
            dishes.find { dish -> dish.dishName == "Boiled eggs" }!!
        } else if (previousDishName != null && previousDishName != "Oatmeal" && previousDishName != "Вівсянка з бананом" && getIndividualSpecialDishUsage(
                allPreviousMeals, "Вівсянка з бананом"
            ) < targetSpecialPercentage && getIndividualSpecialDishUsage(
                allPreviousMeals, "Oatmeal"
            ) < targetSpecialPercentage
        ) {
            dishes.find { dish -> dish.dishName == "Oatmeal" }!!
        } else {
            sortedDishes[0]
        }
    }

    private fun getIndividualSpecialDishUsage(meals: List<Meal>, dishName: String): Double {
        val totalMeals = meals.size
        if (totalMeals == 0) return 0.0

        val dishUsageCount = meals.count { meal ->
            meal.dishes.contains(dishName)
        }

        return (dishUsageCount.toDouble() / totalMeals) * 100
    }

    private suspend fun uploadMealsToFirebase(meals: List<Meal>) {
        for (meal in meals) {
            val formattedDate = formatDateForFirebase(meal.date)
            val mealRef = firebaseDb.child(formattedDate).child(meal.type.toString())
            mealRef.setValue(meal.dishes).await()
        }
    }

    private fun formatDateForFirebase(date: String): String {
        val inputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        return try {
            val dateObj = inputFormat.parse(date)
            outputFormat.format(dateObj!!)
        } catch (e: Exception) {

            println("Error formatting date: $e")
            "invalid_date"
        }
    }

    fun listenForMealUpdates() {
        firebaseDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mealList = mutableListOf<Meal>()

                if (snapshot.exists()) {
                    for (dateSnapshot in snapshot.children) {
                        val dateKey = dateSnapshot.key ?: continue

                        for (mealTypeSnapshot in dateSnapshot.children) {
                            val mealType = mealTypeSnapshot.key ?: continue

                            val dishes = mealTypeSnapshot.getValue(object :
                                GenericTypeIndicator<List<String>>() {}) ?: emptyList()

                            val meal = Meal(
                                date = formatDateFromFirebase(dateKey),
                                type = MealType.valueOf(mealType),
                                dishes = dishes
                            )

                            mealList.add(meal)
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    mealDao.insertAll(mealList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error listening to meals: ${error.message}")
            }
        })
    }

    fun syncRecentMealsFromFirebase() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        calendar.add(Calendar.DAY_OF_YEAR, -5)
        val earliestDate = dateFormat.format(calendar.time)

        firebaseDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mealList = mutableListOf<Meal>()

                if (snapshot.exists()) {
                    for (dateSnapshot in snapshot.children) {
                        val dateKey = dateSnapshot.key ?: continue

                        if (dateKey < earliestDate) continue

                        for (mealTypeSnapshot in dateSnapshot.children) {
                            val mealType = mealTypeSnapshot.key ?: continue

                            val dishes = mealTypeSnapshot.getValue(object :
                                GenericTypeIndicator<List<String>>() {}) ?: emptyList()

                            val meal = Meal(
                                date = formatDateFromFirebase(dateKey),
                                type = MealType.valueOf(mealType),
                                dishes = dishes
                            )

                            mealList.add(meal)
                        }
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    mealDao.insertAll(mealList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error syncing meals from Firebase: ${error.message}")
            }
        })
    }

    private fun formatDateFromFirebase(date: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        return try {
            val dateObj = inputFormat.parse(date)
            outputFormat.format(dateObj!!)
        } catch (e: Exception) {
            println("Error parsing date: $e")
            "invalid_date"
        }
    }

    suspend fun getMealsFromDate(date: String): List<Meal> {
        return mealDao.getMealsFromDate(date)
    }

    suspend fun deleteMeal(meal: Meal) {
        runCatching {
            mealDao.delete(meal)

            val formattedDate = formatDateForFirebase(meal.date)
            val mealRef = firebaseDb.child(formattedDate).child(meal.type.toString())

            mealRef.removeValue().await()
        }.onFailure {
            Log.e(TAG, "Error deleting meal: $meal", it)
        }
    }

    private fun getSortedDishesByEarliestUse(
        dishes: List<Dish>, dateFormat: SimpleDateFormat
    ): List<Dish> {
        return dishes.sortedBy { dish ->
            try {
                val earliestUseDate = dish.useDates.maxByOrNull { date ->
                    try {
                        dateFormat.parse(date)
                    } catch (e: Exception) {
                        null
                    }!!
                }
                earliestUseDate
            } catch (e: Exception) {
                null
            }
        }
    }
}
