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

class MandatoryProductAdapter(private val context: Context, private val mandatoryProducts: MutableList<Product>) : RecyclerView.Adapter<MandatoryProductAdapter.MandatoryProductViewHolder>() {
    private val checkedProducts = HungryMykolaApp.checkedProducts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MandatoryProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mandatory_product, parent, false)
        return MandatoryProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MandatoryProductViewHolder, position: Int) {
        val product = mandatoryProducts[position]
        val language = LocaleManager.getLanguage(context)
        holder.bind(product.getLocalizedProduct(language))
    }

    override fun getItemCount(): Int = mandatoryProducts.size

    inner class MandatoryProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productName: TextView = view.findViewById(R.id.productName)
        private val checkBox: CheckBox = view.findViewById(R.id.checkBox)

        fun bind(name: String) {
            productName.text = name

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = checkedProducts[name] ?: false

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkedProducts[name] = isChecked
            }
        }
    }
}
