package stanyliaINC.hungrymykola.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey
    var dishName: String,
    var dishNameUk: String = "",
    val type: List<MealType>,
    val servings: Int,
    var products: List<Map<String, String>>,
    val recipe: String = "",
    val price: Double = 0.0,
    var lastUseDate: String = "01.01.1970",
    var lastUseDateReserve: String = "01.01.1970") {


    fun getLocalizedDishName(language: String?): String {
        return when (language) {
            "uk" -> dishNameUk
            else -> dishName
        }
    }

    fun setLocalizedDishName(language: String?, name: String) {
        when(language) {
            "uk" -> this.dishNameUk = name
            else -> this.dishName = name
        }
    }
}
