package stanyliaINC.hungrymykola.database

import androidx.room.Database
import androidx.room.RoomDatabase
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.model.Product

@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}