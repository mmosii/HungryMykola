package stanyliaINC.hungrymykola.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.model.Dish

class DishViewModel(private val dishRepository: DishRepository) : ViewModel() {

    fun addDish(dish: Dish) {
        viewModelScope.launch {
            try {
                dishRepository.insert(dish)
            } catch (e: Exception) {
                Log.e("insert: ", "Error inserting: $dish", e)
            }
        }
    }
}
