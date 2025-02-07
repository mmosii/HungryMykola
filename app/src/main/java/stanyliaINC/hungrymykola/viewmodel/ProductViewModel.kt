package stanyliaINC.hungrymykola.viewmodel

import androidx.lifecycle.ViewModel
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.model.Product

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    suspend fun addProduct(product: Product) {
                productRepository.insertProduct(product)
    }

    suspend fun getAllProducts(): List<Product> {
        return productRepository.getAllProducts()
    }

    suspend fun getByName(productName: String): List<Product> {
        return productRepository.getByName(productName)
    }
}
