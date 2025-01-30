package stanyliaINC.hungrymykola.database

import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.model.Product

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertDefaultProducts() {
        val defaultProducts = listOf(
            Product(name = "Milk", amount = 1, unit = "L", url = "https://example.com/milk", price = 1.2),
            Product(name = "Bread", amount = 2, unit = "pieces", url = "https://example.com/bread", price = 0.8)
        )

        defaultProducts.forEach { productDao.insert(it) }
    }
}