package stanyliaINC.hungrymykola.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Int = 0,
    val unit: String? = null,
    val url: String? = null,
    val price: Double = 0.0
)