package stanyliaINC.hungrymykola.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    var name: String = "",
    var nameUk: String = "",
    var amount: Int = 0,
    var unit: Units? = null,
    var url: String? = null,
    var price: Double = 0.0,
    var priceUpdateDate: String = "1990-10-10"
) {
    constructor() : this("", "", 0, null, null, 0.0, "1990-10-10")

    fun getLocalizedProduct(language: String?): String {
        return when (language) {
            "uk" -> nameUk
            else -> name
        }
    }
}
