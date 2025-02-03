package stanyliaINC.hungrymykola.database

import android.util.Log
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.model.Product
import stanyliaINC.hungrymykola.model.Units

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertDefaultProducts() {
        val defaultProducts = listOf(
            Product("Egg", "Яйце", 10, Units.PCS, "https://example.com/eggs", 60.0),
            Product("Milk","Молоко",  900, Units.ML, "https://example.com/milk", 44.0),

            Product("Tomato","Помідор",  1000, Units.G, "https://example.com/tomato", 95.0),
            Product("Cucumber", "Огірок", 1000, Units.G, "https://example.com/cucumber", 85.0),
            Product("Olive Oil", "Оливкова олія", 1000, Units.ML, "https://example.com/oliveoil", 500.0),

            Product("Flour", "Борошно",  1000, Units.G, "https://example.com/flour", 35.0),
            Product("Sugar","Цукор",  500, Units.G, "https://example.com/sugar", 20.0),

            Product("Spaghetti", "Спагетті",  1000, Units.G, "https://example.com/spaghetti", 150.0),
            Product("Tomato Sauce", "Томатна паста", 500, Units.ML, "https://example.com/tomatosauce", 75.0),

            Product("Chicken Breast", "Куряче філе", 1000, Units.G, "https://example.com/chicken", 120.0),
            Product("Carrot", "Морква",  500, Units.G, "https://example.com/carrot", 20.0),
            Product("Onion","Цибуля",  500, Units.G, "https://example.com/onion", 30.0),
            Product("Garlic","Часник",  100, Units.G, "https://example.com/garlic", 10.0),
            Product("Water", "Вода", 1000, Units.ML, "https://example.com/water", 0.0),

            Product("Bread", "Хліб", 400, Units.G, "https://example.com/bread", 0.8),
            Product("Cheese", "Сир", 200, Units.G, "https://example.com/cheese", 100.0),
            Product("Butter", "Масло", 200, Units.G, "https://example.com/butter", 70.0),

            Product("Beef", "Яловичина", 500, Units.G, "https://example.com/beef", 300.0),
            Product("Salt", "Сіль", 200, Units.G, "https://example.com/salt", 10.0),
            Product("Pepper", "Перець", 100, Units.G, "https://example.com/pepper", 20.0),

            Product("Apple", "Яблуко", 1000, Units.G, "https://example.com/apple", 50.0),
            Product("Banana", "Банан", 1000, Units.G, "https://example.com/banana", 40.0),
            Product("Orange", "Апельсин", 1000, Units.G, "https://example.com/orange", 60.0),

            Product("Broccoli", "Брокколі", 500, Units.G, "https://example.com/broccoli", 35.0),
            Product("Bell Pepper", "Перець болгарський", 500, Units.G, "https://example.com/bellpepper", 25.0),
            Product("Soy Sauce", "Соєвий соус", 500, Units.G, "https://example.com/soysauce", 100.0),
            Product("Avocado", "Авокадо", 500, Units.G, "https://example.com/soysauce", 100.0)
        )

        defaultProducts.forEach { product ->
            runCatching {
                productDao.insert(product)
            }.onFailure { e ->
                Log.e("DB_ERROR", "Error inserting: $product", e)
            }
        }
    }

    suspend fun insertProduct(product: Product) {
        try {
            productDao.insert(product)
        } catch (e: Exception) {
            Log.e("insert: ", "Error inserting: $product", e)
        }
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun getByName(name: String): List<Product> {
        return productDao.getByName(name)
    }

    suspend fun update(product: Product) {
        productDao.update(product)
    }
}