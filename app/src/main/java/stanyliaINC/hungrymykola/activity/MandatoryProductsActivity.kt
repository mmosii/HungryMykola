package stanyliaINC.hungrymykola.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.ProductDao
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.databinding.ActivityMandatoryProductsBinding
import stanyliaINC.hungrymykola.model.Product
import stanyliaINC.hungrymykola.utils.MandatoryProductAdapter

class MandatoryProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMandatoryProductsBinding
    private lateinit var adapter: MandatoryProductAdapter
    private lateinit var productDao: ProductDao
    private lateinit var productRepository: ProductRepository
    private lateinit var productNameAutoCompleteTextView: AutoCompleteTextView
    private var mandatoryProducts = mutableListOf<Product>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))
        binding = ActivityMandatoryProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productNameAutoCompleteTextView = binding.productNameAutoCompleteTextView
        val database = DatabaseProvider.getDatabase(applicationContext)
        productDao = database.productDao()
        productRepository = ProductRepository(productDao)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedProductsSet = prefs.getStringSet("mandatoryProducts", emptySet()) ?: emptySet()

        lifecycleScope.launch {
            val allProducts = productRepository.getAllProducts()
            val productNames =
                allProducts.map { it.name } + allProducts.map { it.nameUk }.toSet().toList()
            val autoCompleteAdapter = ArrayAdapter(
                this@MandatoryProductsActivity,
                android.R.layout.simple_dropdown_item_1line,
                productNames
            )
            productNameAutoCompleteTextView.setAdapter(autoCompleteAdapter)
            mandatoryProducts = savedProductsSet.mapNotNull { entry ->
                val parts = entry.split("|")
                allProducts.find { it.name == parts[0] || it.nameUk == parts[1] }
            }.toMutableList()

            adapter = MandatoryProductAdapter(this@MandatoryProductsActivity, mandatoryProducts)

            binding.mandatoryProductRecyclerView.layoutManager = LinearLayoutManager(this@MandatoryProductsActivity)
            binding.mandatoryProductRecyclerView.adapter = adapter

            binding.plusButton.setOnClickListener {
                val text = productNameAutoCompleteTextView.text.toString()
                if (text.isNotEmpty()) {
                    lifecycleScope.launch {
                        val productToAdd = productRepository.getByName(text).first()
                        if (!mandatoryProducts.contains(productToAdd)) {
                            mandatoryProducts.add(productToAdd)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this@MandatoryProductsActivity,
                                getString(R.string.new_mandatory_product_added), Toast.LENGTH_SHORT).show()
                            productNameAutoCompleteTextView.text.clear()
                            prefs.edit().putStringSet("mandatoryProducts", mandatoryProducts.map { product ->  "${product.name}|${product.nameUk}" }.toSet()).apply()
                        }
                    }
                } else {
                    Toast.makeText(this@MandatoryProductsActivity,
                        getString(R.string.not_valid_product_name), Toast.LENGTH_SHORT).show()
                }
            }

            binding.minusButton.setOnClickListener {
                val text = productNameAutoCompleteTextView.text.toString()
                if (text.isNotEmpty()) {
                    lifecycleScope.launch {
                        val productToAdd = productRepository.getByName(text).first()
                        if (mandatoryProducts.contains(productToAdd)) {
                            mandatoryProducts.remove(productToAdd)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(this@MandatoryProductsActivity,
                                getString(R.string.mandatory_product_removed), Toast.LENGTH_SHORT).show()
                            productNameAutoCompleteTextView.text.clear()
                            prefs.edit().putStringSet("mandatoryProducts", mandatoryProducts.map { product ->  "${product.name}|${product.nameUk}" }.toSet()).apply()
                        }
                    }
                } else {
                    Toast.makeText(this@MandatoryProductsActivity, getString(R.string.not_valid_product_name), Toast.LENGTH_SHORT).show()
                }
            }

            binding.backButton.setOnClickListener {
                finish()
            }

            adapter.notifyDataSetChanged()
        }
    }
}
