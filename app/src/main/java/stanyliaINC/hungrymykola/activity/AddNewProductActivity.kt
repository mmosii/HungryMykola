package stanyliaINC.hungrymykola.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.model.Product
import stanyliaINC.hungrymykola.model.Units
import stanyliaINC.hungrymykola.viewmodel.ProductViewModel
import stanyliaINC.hungrymykola.viewmodel.factory.ProductViewModelFactory

class AddNewProductActivity : AppCompatActivity() {

    private lateinit var productNameEditText: EditText
    private lateinit var productAmountEditText: EditText
    private lateinit var productUnitEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productUrlEditText: EditText

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(
            ProductRepository(
                DatabaseProvider.getDatabase(applicationContext).productDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        setContentView(R.layout.activity_add_new_product)

        productNameEditText = findViewById(R.id.productNameEditText)
        productAmountEditText = findViewById(R.id.productAmountEditText)
        productUnitEditText = findViewById(R.id.productUnitEditText)
        productPriceEditText = findViewById(R.id.productPriceEditText)
        productUrlEditText = findViewById(R.id.productUrlEditText)

        findViewById<View>(R.id.saveNewProductButton).setOnClickListener {
            saveProduct()
        }
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString()
        val amount = productAmountEditText.text.toString().toIntOrNull()
        val unit = productUnitEditText.text.toString()
        val price = productPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
        val url = productUrlEditText.text.toString()

        if (name.isEmpty() || amount == null || unit.isEmpty()) {
            Toast.makeText(this, R.string.please_fill_in_all_product_fields, Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            name = name,
            nameUk = name,
            amount = amount,
            unit = Units.fromLocalizedName(this@AddNewProductActivity, unit),
            price = price,
            url = url
        )

        lifecycleScope.launch {
            try {
                productViewModel.addProduct(product)
                Toast.makeText(
                    this@AddNewProductActivity,
                    getString(R.string.product_added_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddNewProductActivity,
                    getString(R.string.error_adding_product, e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
