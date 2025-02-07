package stanyliaINC.hungrymykola.model

import android.content.Context
import stanyliaINC.hungrymykola.R

enum class Units {
    PCS,
    ML,
    G;

    fun getLocalizedUnit(context: Context): String {
        return when (this) {
            PCS -> context.getString(R.string.unit_pcs)
            ML -> context.getString(R.string.unit_ml)
            G -> context.getString(R.string.unit_g)
        }
    }
    companion object {
        fun fromLocalizedName(context: Context, localizedName: String): Units? {
            return Units.entries.find { it.getLocalizedUnit(context) == localizedName }
        }
    }
}
