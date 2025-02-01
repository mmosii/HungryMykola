package stanyliaINC.hungrymykola.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.database.ProductRepository

class MainViewModel(
    private val productRepository: ProductRepository,
    private val dishRepository: DishRepository
) : ViewModel() {

    fun insertDefaultData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    dishRepository.insertDefaultDishes()
                    productRepository.insertDefaultProducts()
                }
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error inserting default data", e)
            }
        }
    }
}
