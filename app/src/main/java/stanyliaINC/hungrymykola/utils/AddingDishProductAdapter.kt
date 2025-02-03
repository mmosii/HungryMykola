package stanyliaINC.hungrymykola.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stanyliaINC.hungrymykola.R

class AddingDishProductAdapter(
    private val productList: MutableList<Map<String, String>>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<AddingDishProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val productAmountTextView: TextView = view.findViewById(R.id.productAmountTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteProductButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productNameTextView.text = product["name"]
        holder.productAmountTextView.text = "${product["amount"]} ${product["unit"]}"

        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = productList.size
}
