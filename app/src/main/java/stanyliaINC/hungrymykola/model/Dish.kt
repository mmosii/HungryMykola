package stanyliaINC.hungrymykola.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class Dish(
    @PrimaryKey
    var dishName: String = "",
    var dishNameUk: String = "",
    val type: List<MealType> = emptyList(),
    val servings: Int = 0,
    var products: List<Map<String, String>> = emptyList(),
    val recipe: String = "",
    val price: Double = 0.0,
    var useDates: List<String> = listOf("01.01.1970")
) {
    constructor() : this("", "", emptyList(), 0, emptyList(), "", 0.0, listOf("01.01.1970"))

    fun getLocalizedDishName(language: String?): String {
        return when (language) {
            "uk" -> dishNameUk
            else -> dishName
        }
    }
}
