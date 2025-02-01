package stanyliaINC.hungrymykola.service

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import stanyliaINC.hungrymykola.dao.DishDao
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.database.DatabaseProvider
import java.math.BigDecimal
import java.math.RoundingMode

class PriceUpdateService(context: Context) {
    private val dishDao: DishDao
    private val productDao: ProductDao

    init {
        val database = DatabaseProvider.getDatabase(context)
        dishDao = database.dishDao()
        productDao = database.productDao()
    }

    suspend fun updateDishPrice(dishName: String): Double? {
        return withContext(Dispatchers.IO) {
            val price = calculateDishPrice(dishName) ?: return@withContext null
            val roundedPrice = BigDecimal(price).setScale(2, RoundingMode.HALF_UP).toDouble()
            val dish = dishDao.getDishByName(dishName)
            dish?.let {
                val updatedDish = it.copy(price = roundedPrice)
                dishDao.update(updatedDish)
            }
            roundedPrice
        }
    }

    private suspend fun calculateDishPrice(dish: String): Double? {
        val dishByName = dishDao.getDishByName(dish)
        val dishProducts = dishByName?.products
        val products = dishProducts?.map { it["name"] ?: "" }?.map { name ->
            productDao.getByName(name)
        }?.flatten()
        val totalPrice = dishProducts?.mapNotNull { dishProduct ->
            val productName = dishProduct["name"] ?: return@mapNotNull null
            val amount = dishProduct["amount"]?.toIntOrNull() ?: return@mapNotNull null
            val unit = dishProduct["unit"]

            val product = products?.firstOrNull { it.name == productName }

            if (product != null) {
                val pricePerUnit = product.price
                val productAmount = product.amount
                val priceForUsedAmount = (pricePerUnit * amount) / productAmount

                mapOf(
                    "name" to productName,
                    "amountUsed" to amount,
                    "unit" to unit,
                    "priceForUsedAmount" to priceForUsedAmount
                )
            } else {
                null
            }
        }?.sumByDouble { it["priceForUsedAmount"] as Double }
        return totalPrice
    }
}
