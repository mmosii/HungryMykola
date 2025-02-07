package stanyliaINC.hungrymykola.dao

import androidx.room.*
import stanyliaINC.hungrymykola.model.Meal

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meals: List<Meal>)

    @Update
    suspend fun update(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Query("SELECT * FROM meals WHERE date = :date AND type = :type")
    suspend fun getMealByDateAndType(date: String, type: String): Meal?

    @Query("SELECT * FROM meals WHERE date = :date")
    suspend fun getMealsByDate(date: String): List<Meal>

    @Query("SELECT * FROM meals WHERE date >= :date ORDER BY date ASC")
    suspend fun getMealsFromDate(date: String): List<Meal>

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<Meal>

    @Query("DELETE FROM meals WHERE date = :date AND type = :type")
    suspend fun deleteMealByDateAndType(date: String, type: String)

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()

    @Query("DELETE FROM meals WHERE date > :date AND type = :type")
    suspend fun removeMealsByTypeAndDateGreaterThan(date: String, type: String)
}
