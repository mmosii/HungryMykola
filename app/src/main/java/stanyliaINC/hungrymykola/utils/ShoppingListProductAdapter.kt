package stanyliaINC.hungrymykola.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stanyliaINC.hungrymykola.HungryMykolaApp
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.model.Product

class ShoppingListProductAdapter(
    private val context: Context,
    private val productList: List<Pair<Product, Double>>
) : RecyclerView.Adapter<ShoppingListProductAdapter.ProductViewHolder>() {

    private val checkedProducts = HungryMykolaApp.checkedProducts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productName: TextView = view.findViewById(R.id.productName)
        private val productPrice: TextView = view.findViewById(R.id.productPrice)
        private val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(product: Pair<Product, Double>) {
            val productKey = product.first.getLocalizedProduct(LocaleManager.getLanguage(context))

            productName.text = productKey
            productPrice.text = context.getString(
                R.string.shopping_list_prod_view,
                String.format(context.getString(R.string._2f), (product.first.amount * (product.second / product.first.price))),
                product.first.unit?.getLocalizedUnit(context),
                String.format(context.getString(R.string._2f), product.second)
            )

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = checkedProducts[productKey] ?: false

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkedProducts[productKey] = isChecked
            }
        }
    }
}
