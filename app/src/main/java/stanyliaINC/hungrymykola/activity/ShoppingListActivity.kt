package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.databinding.ActivityShoppingListBinding
import stanyliaINC.hungrymykola.utils.ShoppingListCalendarAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingListBinding
    private lateinit var adapter: ShoppingListCalendarAdapter
    private var selectedRange: Pair<LocalDate, LocalDate>? = null
    private lateinit var dateFormat: DateTimeFormatter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this))
        dateFormat = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault())

        binding = ActivityShoppingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCalendar()
        binding.buttonShowProducts.setOnClickListener {
            if ( selectedRange != null ) {
                val intent = Intent(this, ProductListActivity::class.java)

                intent.putExtra("startDate", dateFormat.format(selectedRange?.first).toString())
                intent.putExtra("endDate", dateFormat.format(selectedRange?.second).toString())
                startActivity(intent)
            } else {
                Toast.makeText(this@ShoppingListActivity, getString(R.string.choose_day), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCalendar() {
        val days = getNext15Days()
        adapter = ShoppingListCalendarAdapter(this@ShoppingListActivity, days) { start, end ->
            selectedRange = Pair(start, end)
            dateFormat.format(start)
            if (start == end) {
                binding.selectedDateRange.text =
                    getString(R.string.selected, dateFormat.format(start))
            }  else {
                binding.selectedDateRange.text = getString(
                    R.string.selected_range,
                    dateFormat.format(start),
                    dateFormat.format(end)
                )
            }
        }

        val layoutManager = GridLayoutManager(this, 5)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = adapter
    }

    private fun getNext15Days(): List<LocalDate> {
        val today = LocalDate.now()
        return List(15) { today.plusDays(it.toLong()) }
    }
}
