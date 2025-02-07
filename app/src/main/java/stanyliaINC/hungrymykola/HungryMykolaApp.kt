package stanyliaINC.hungrymykola

import android.app.Application

class HungryMykolaApp : Application() {
    companion object {
        val checkedProducts: MutableMap<String, Boolean> = mutableMapOf()
    }

    override fun onCreate() {
        super.onCreate()
        checkedProducts.clear()
    }
}
