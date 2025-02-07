package stanyliaINC.hungrymykola.model

import android.content.Context
import stanyliaINC.hungrymykola.R

enum class MealType {
    BREAKFAST,
    SNACK,
    LUNCH,
    SANDWICH,
    DINNER;

    fun getLocalizedDishName(context: Context): String {
        return when (this) {
            BREAKFAST -> context.getString(R.string.dish_name_breakfast)
            LUNCH -> context.getString(R.string.dish_name_lunch)
            SANDWICH -> context.getString(R.string.dish_name_sandwich)
            DINNER -> context.getString(R.string.dish_name_dinner)
            SNACK -> context.getString(R.string.dish_name_snack)
        }
    }
}
