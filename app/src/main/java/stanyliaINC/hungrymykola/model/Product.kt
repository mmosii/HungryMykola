package stanyliaINC.hungrymykola.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val name: String = "",
    val nameUk: String = "",
    val amount: Int = 0,
    val unit: Units? = null,
    val url: String? = null,
    val price: Double = 0.0) {

    fun getLocalizedProduct(language: String?): String {
        return when (language) {
            "uk" -> nameUk
            else -> name
        }
    }
}