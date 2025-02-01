package stanyliaINC.hungrymykola.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.viewmodel.DishViewModel

class DishViewModelFactory(private val dishRepository: DishRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DishViewModel(dishRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
