package stanyliaINC.hungrymykola.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.MealDao
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.model.Product

@Database(entities = [Dish::class, Product::class, Meal::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dishDao(): DishDao
    abstract fun productDao(): ProductDao
    abstract fun mealDao(): MealDao
}
