package stanyliaINC.hungrymykola.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import stanyliaINC.hungrymykola.model.Product

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE name = :name")
    suspend fun getByName(name: String): List<Product>

    @Delete
    suspend fun delete(product: Product)
}