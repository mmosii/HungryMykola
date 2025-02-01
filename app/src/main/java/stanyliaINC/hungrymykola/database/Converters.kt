package stanyliaINC.hungrymykola.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import stanyliaINC.hungrymykola.model.MealType

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromProductList(value: List<Map<String, String>>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toProductList(value: String?): List<Map<String, String>>? {
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromMealList(value: List<MealType>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMealList(value: String?): List<MealType>? {
        val type = object : TypeToken<List<MealType>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
