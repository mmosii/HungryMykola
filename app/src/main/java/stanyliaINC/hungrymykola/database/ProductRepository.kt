package stanyliaINC.hungrymykola.database

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.model.Product

class ProductRepository(private val productDao: ProductDao) {

    private val productsFirebaseReference =
        FirebaseDatabase.getInstance("")
            .getReference("products")
    private val mandatoryProductsFirebaseReference =
        FirebaseDatabase.getInstance("")
            .getReference("mandatoryProducts")

    companion object {
        private const val TAG = "ProductRepository"
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun getByName(name: String): List<Product> {
        return productDao.getByName(name)
    }

    suspend fun update(product: Product) {
        insertProduct(product)
    }

    fun listenForProductUpdates() {
        productsFirebaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { data ->
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            productDao.insert(product)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("listenForProductUpdates", "Database error: ${error.message}")
            }
        })
    }

    suspend fun insertProduct(product: Product) {
        runCatching {
            productDao.insert(product)
            productsFirebaseReference.child(product.name).setValue(product)
        }.onSuccess {
            Log.d(TAG, "Successfully inserted $product")
        }.onFailure {
            Log.e(TAG, "Error inserting products: product", it)
        }
    }

    fun syncAllProductsFromFirebase() {
        productsFirebaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Product>()
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    productDao.insertAll(productList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error syncing products: ${error.message}")
            }
        })
    }

    fun syncMandatoryProductsFromFirebase(context: Context) {
        mandatoryProductsFirebaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val savedProductsSet =
                        snapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                            ?: emptyList()
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putStringSet("mandatoryProducts", savedProductsSet.toSet()).apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error syncing products: ${error.message}")
            }
        })
    }

    fun listenForMandatoryProductUpdates(context: Context) {
        mandatoryProductsFirebaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val updatedProductsSet =
                        snapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                            ?: emptyList()
                    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putStringSet("mandatoryProducts", updatedProductsSet.toSet())
                        .apply()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error listening for product updates: ${error.message}")
            }
        })
    }
}
