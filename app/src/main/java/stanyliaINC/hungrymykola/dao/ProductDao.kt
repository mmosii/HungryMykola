package stanyliaINC.hungrymykola.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import stanyliaINC.hungrymykola.model.Product

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE name = :name OR nameUk = :name")
    suspend fun getByName(name: String): List<Product>

    @Delete
    suspend fun delete(product: Product)

    @Update
    suspend fun update(product:Product)
}