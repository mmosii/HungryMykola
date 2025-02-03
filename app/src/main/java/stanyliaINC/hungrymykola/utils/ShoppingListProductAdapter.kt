package stanyliaINC.hungrymykola.utils

import LocaleManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.model.Product

class ShoppingListProductAdapter(private val context: Context, private val productList: List<Pair<Product, Double>>) : RecyclerView.Adapter<ShoppingListProductAdapter.ProductViewHolder>() {

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
        fun bind(product: Pair<Product, Double>) {
            productName.text = product.first.getLocalizedProduct(LocaleManager.getLanguage(context))
            productPrice.text = context.getString(
                R.string.shopping_list_prod_view,
                (product.first.amount * (product.second / product.first.price)).toString(),
                product.first.unit?.getLocalizedUnit(context),
                product.second.toString()
            )
        }
    }
}
