package stanyliaINC.hungrymykola.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.database.MealRepository

class MealViewModel(private val mealRepository: MealRepository,  ) : ViewModel() {

    fun insertMealsForDate(date: String) {
        viewModelScope.launch {
            try {
                Log.d("MealViewModel", "calling addMealsForDate for $date")
                mealRepository.addMealsForDate(date)
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error inserting default data", e)
            }
        }
    }
}
