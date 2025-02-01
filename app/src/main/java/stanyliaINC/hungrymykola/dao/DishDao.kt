package stanyliaINC.hungrymykola.dao

import androidx.room.*
import stanyliaINC.hungrymykola.model.Dish

@Dao
interface DishDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dish: Dish)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dishes: List<Dish>)

    @Query("SELECT * FROM dishes")
    suspend fun getAllDishes(): List<Dish>

    @Query("SELECT * FROM dishes WHERE dishName = :name OR dishNameUk = :name")
    suspend fun getDishByName(name: String): Dish?

    @Delete
    suspend fun delete(dish: Dish)

    @Update
    suspend fun update(dish: Dish)
}
