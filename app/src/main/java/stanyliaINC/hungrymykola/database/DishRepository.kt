package stanyliaINC.hungrymykola.database

import android.util.Log
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.model.*

class DishRepository(private val dishDao: DishDao) {

    suspend fun insertDefaultDishes() {
        val defaultDishes = listOf(
            Dish(
                dishName = "Omelette",
                dishNameUk = "Омлет",
                type = listOf(MealType.BREAKFAST),
                servings = 1,
                products = listOf(
                    mapOf("name" to "Egg", "amount" to "2", "unit" to "pcs"),
                    mapOf("name" to "Milk", "amount" to "100", "unit" to "ml")
                ),
                recipe = "Whisk eggs and milk, fry on a pan."
            ),

            Dish(
                dishName = "Salad",
                dishNameUk = "Салат",
                type = listOf(MealType.LUNCH),
                servings = 2,
                products = listOf(
                    mapOf("name" to "Tomato", "amount" to "150", "unit" to "g"),
                    mapOf("name" to "Cucumber", "amount" to "150", "unit" to "g"),
                    mapOf("name" to "Olive Oil", "amount" to "10", "unit" to "ml")
                ),
                recipe = "Chop vegetables, add oil, mix well."
            ),

            Dish(
                dishName = "Pancakes",
                dishNameUk = "Млинці",
                type = listOf(MealType.BREAKFAST),
                servings = 3,
                products = listOf(
                    mapOf("name" to "Flour", "amount" to "100", "unit" to "g"),
                    mapOf("name" to "Egg", "amount" to "2", "unit" to "pcs"),
                    mapOf("name" to "Milk", "amount" to "200", "unit" to "ml"),
                    mapOf("name" to "Sugar", "amount" to "10", "unit" to "g")
                ),
                recipe = "Mix flour, eggs, milk, and sugar. Fry in a pan until golden brown."
            ),

            Dish(
                dishName = "Spaghetti",
                dishNameUk = "Спагеті",
                type = listOf(MealType.DINNER),
                servings = 2,
                products = listOf(
                    mapOf("name" to "Spaghetti", "amount" to "200", "unit" to "g"),
                    mapOf("name" to "Tomato Sauce", "amount" to "100", "unit" to "ml"),
                    mapOf("name" to "Cheese", "amount" to "50", "unit" to "g")
                ),
                recipe = "Boil spaghetti, mix with tomato sauce, and top with cheese."
            ),

            Dish(
                dishName = "Chicken Soup",
                dishNameUk = "Курячий суп",
                type = listOf(MealType.LUNCH),
                servings = 4,
                products = listOf(
                    mapOf("name" to "Chicken", "amount" to "500", "unit" to "g"),
                    mapOf("name" to "Carrot", "amount" to "100", "unit" to "g"),
                    mapOf("name" to "Onion", "amount" to "1", "unit" to "pcs"),
                    mapOf("name" to "Garlic", "amount" to "2", "unit" to "cloves"),
                    mapOf("name" to "Water", "amount" to "1", "unit" to "L")
                ),
                recipe = "Boil chicken and vegetables in water for 40 minutes."
            ),

            Dish(
                dishName = "Grilled Cheese Sandwich",
                dishNameUk = "Сендвіч з сирами",
                type = listOf(MealType.SNACK),
                servings = 1,
                products = listOf(
                    mapOf("name" to "Bread", "amount" to "2", "unit" to "pcs"),
                    mapOf("name" to "Cheese", "amount" to "50", "unit" to "g"),
                    mapOf("name" to "Butter", "amount" to "20", "unit" to "g")
                ),
                recipe = "Put cheese between two slices of bread, grill with butter until golden."
            ),

            Dish(
                dishName = "Beef Steak",
                dishNameUk = "Стейк з яловичини дикого буйвола який бігав лісом арізони",
                type = listOf(MealType.DINNER),
                servings = 2,
                products = listOf(
                    mapOf("name" to "Beef", "amount" to "300", "unit" to "g"),
                    mapOf("name" to "Salt", "amount" to "5", "unit" to "g"),
                    mapOf("name" to "Pepper", "amount" to "2", "unit" to "g"),
                    mapOf("name" to "Olive Oil", "amount" to "10", "unit" to "ml")
                ),
                recipe = "Season beef with salt and pepper, then grill for 5 minutes on each side."
            ),

            Dish(
                dishName = "Fruit Salad",
                dishNameUk = "Фруктовий салат",
                type = listOf(MealType.SNACK),
                servings = 1,
                products = listOf(
                    mapOf("name" to "Apple", "amount" to "1", "unit" to "pcs"),
                    mapOf("name" to "Banana", "amount" to "1", "unit" to "pcs"),
                    mapOf("name" to "Orange", "amount" to "1", "unit" to "pcs")
                ),
                recipe = "Chop fruit and mix together."
            ),

            Dish(
                dishName = "Vegetable Stir Fry",
                dishNameUk = "Овочевий стір-фрай",
                type = listOf(MealType.LUNCH),
                servings = 2,
                products = listOf(
                    mapOf("name" to "Carrot", "amount" to "100", "unit" to "g"),
                    mapOf("name" to "Broccoli", "amount" to "100", "unit" to "g"),
                    mapOf("name" to "Bell Pepper", "amount" to "1", "unit" to "pcs"),
                    mapOf("name" to "Soy Sauce", "amount" to "15", "unit" to "ml")
                ),
                recipe = "Stir fry vegetables in soy sauce and serve hot."
            )
        )

        dishDao.insertAll(defaultDishes)
    }


    suspend fun insert(dish: Dish) {
        try {
            dishDao.insert(dish)
        } catch (e: Exception) {
            Log.e("insert: ", "Error inserting: $dish", e)
        }
    }

    suspend fun getAllDishes(): List<Dish> {
        return dishDao.getAllDishes()
    }

}
