package stanyliaINC.hungrymykola.model

import androidx.room.Entity

@Entity(
    tableName = "meals",
    primaryKeys = ["date", "type"]
)
data class Meal(
    val type: MealType,
    val dishes: List<String>,
    val date: String
)