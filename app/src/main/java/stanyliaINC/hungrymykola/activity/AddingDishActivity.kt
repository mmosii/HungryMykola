package stanyliaINC.hungrymykola.activity

import stanyliaINC.hungrymykola.utils.LocaleManager
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.database.DatabaseProvider
import stanyliaINC.hungrymykola.database.DishRepository
import stanyliaINC.hungrymykola.database.ProductRepository
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.MealType
import stanyliaINC.hungrymykola.utils.AddingDishProductAdapter
import stanyliaINC.hungrymykola.viewmodel.DishViewModel
import stanyliaINC.hungrymykola.viewmodel.ProductViewModel
import stanyliaINC.hungrymykola.viewmodel.factory.DishViewModelFactory
import stanyliaINC.hungrymykola.viewmodel.factory.ProductViewModelFactory

class AddingDishActivity : AppCompatActivity() {

    private val dishViewModel: DishViewModel by viewModels() {
        DishViewModelFactory(DishRepository(DatabaseProvider.getDatabase(applicationContext).dishDao()))
    }

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(ProductRepository(DatabaseProvider.getDatabase(applicationContext).productDao()))
    }

    private lateinit var dishNameEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var productNameAutoCompleteTextView: AutoCompleteTextView
    private lateinit var productAmountEditText: EditText
    private lateinit var recipeEditText: EditText
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var addingDishProductAdapter: AddingDishProductAdapter
    private lateinit var mealTypeContainer: GridLayout
    private val selectedMealTypes = mutableListOf<MealType>()
    private val productList = mutableListOf<Map<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))

        setContentView(R.layout.activity_adding_dish)
        mealTypeContainer = findViewById(R.id.mealTypeContainer)
        dishNameEditText = findViewById(R.id.dishNameEditText)
        servingsEditText = findViewById(R.id.servingsEditText)
        productNameAutoCompleteTextView = findViewById(R.id.productNameAutoCompleteTextView)
        productAmountEditText = findViewById(R.id.productAmountEditText)
        recipeEditText = findViewById(R.id.recipeEditText)
        productRecyclerView = findViewById(R.id.productRecyclerView)

        productRecyclerView.layoutManager = LinearLayoutManager(this)
        addingDishProductAdapter =
            AddingDishProductAdapter(productList) { position -> removeProduct(position) }
        productRecyclerView.adapter = addingDishProductAdapter

        addMealTypeBoxes()

        lifecycleScope.launch {
            val products = productViewModel.getAllProducts()
            val productNames =
                products.map { it.name } + products.map { it.nameUk }.toSet().toList()
            val adapter = ArrayAdapter(
                this@AddingDishActivity,
                android.R.layout.simple_dropdown_item_1line,
                productNames
            )
            productNameAutoCompleteTextView.setAdapter(adapter)
        }

        findViewById<View>(R.id.addProductButton).setOnClickListener { addProduct() }
        findViewById<View>(R.id.saveButton).setOnClickListener { saveDish() }
        findViewById<View>(R.id.addNewProductButton).setOnClickListener {
            startActivity(Intent(this, AddNewProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val products = productViewModel.getAllProducts()
            val productNames =
                products.map { it.name } + products.map { it.nameUk }.toSet().toList()
            val adapter = ArrayAdapter(
                this@AddingDishActivity,
                android.R.layout.simple_dropdown_item_1line,
                productNames
            )
            productNameAutoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun addMealTypeBoxes() {
        MealType.entries
            .forEach { mealType ->
                val mealTypeBox = createMealTypeBox(mealType)
                mealTypeContainer.addView(mealTypeBox)
            }
    }

    private fun createMealTypeBox(mealType: MealType): View {
        val context = this

        val mealTypeBox = TextView(context).apply {
            text = mealType.getLocalizedDishName(context)
            setTextColor(Color.WHITE)
            textSize = 11F
            setPadding(2, 5, 2, 8)
            isClickable = true
            isFocusable = true
            gravity = Gravity.CENTER
        }

        val layoutParams = GridLayout.LayoutParams().apply {
            width = 0
            height = 100
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED)
            setMargins(2, 2, 2, 2)
        }

        mealTypeBox.layoutParams = layoutParams
        val greyBackground = ContextCompat.getDrawable(context, R.drawable.box_background_grey)
        val greenBackground = ContextCompat.getDrawable(context, R.drawable.box_background_green)
        mealTypeBox.background = greyBackground

        mealTypeBox.setOnClickListener {
            toggleMealTypeSelection(mealType, mealTypeBox, greyBackground, greenBackground)
        }

        return mealTypeBox
    }

    private fun toggleMealTypeSelection(
        mealType: MealType,
        selectedBox: TextView,
        greyBackground: Drawable?,
        greenBackground: Drawable?
    ) {
        selectedMealTypes.clear()
        mealTypeContainer.children.forEach { view ->
            if (view is TextView) {
                view.background = greyBackground
            }
        }

        selectedMealTypes.add(mealType)
        selectedBox.background = greenBackground
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addProduct() {
        val productName = productNameAutoCompleteTextView.text.toString()
        val productAmount = productAmountEditText.text.toString().toIntOrNull()

        if (productName.isEmpty() || productAmount == null) {
            Toast.makeText(
                this,
                getString(R.string.please_fill_in_all_product_fields), Toast.LENGTH_SHORT
            ).show()
            return
        }

        lifecycleScope.launch {
            val products = productViewModel.getByName(productName)
            if (products.isNotEmpty()) {
                val product = products[0]
                val productUnit = product.unit?.getLocalizedUnit(this@AddingDishActivity) ?: ""
                val productMap = mapOf(
                    "name" to product.getLocalizedProduct(LocaleManager.getLanguage(this@AddingDishActivity)),
                    "amount" to productAmount.toString(),
                    "unit" to productUnit
                )
                productList.add(productMap)
                addingDishProductAdapter.notifyDataSetChanged()
                productNameAutoCompleteTextView.text.clear()
                productAmountEditText.text.clear()
            } else {
                Toast.makeText(
                    this@AddingDishActivity,
                    getString(R.string.product_not_found_add_it_manually),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeProduct(position: Int) {
        productList.removeAt(position)
        addingDishProductAdapter.notifyDataSetChanged()
    }

    private fun saveDish() {
        val dishNameUk = dishNameEditText.text.toString()
        val dishName = dishNameEditText.text.toString()
        val servings = servingsEditText.text.toString().toIntOrNull()
        val type = selectedMealTypes
        val recipe = recipeEditText.text.toString()

        if ((dishName.isEmpty() && dishNameUk.isEmpty()) || servings == null || productList.isEmpty() || type.isEmpty() ) {
            Toast.makeText(this, getString(R.string.please_fill_in_all_fields), Toast.LENGTH_SHORT)
                .show()
            return
        }

        val dish = Dish(dishName, dishNameUk, type, servings, productList, recipe)

        lifecycleScope.launch {
            try {
                dishViewModel.addDish(dish)
                Toast.makeText(
                    this@AddingDishActivity,
                    getString(R.string.dish_added_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AddingDishActivity,
                    "Error adding dish: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
