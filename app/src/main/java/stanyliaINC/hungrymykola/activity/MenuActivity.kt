package stanyliaINC.hungrymykola.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import stanyliaINC.hungrymykola.databinding.ActivityMenuBinding
import stanyliaINC.hungrymykola.model.Dish
import stanyliaINC.hungrymykola.model.Meal
import stanyliaINC.hungrymykola.utils.CalendarAdapter
import java.text.SimpleDateFormat
import java.util.*

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val calendar = Calendar.getInstance()
    private var selectedPeriod = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val language = LocaleManager.getLanguage(this)
        if (language != null) {
            LocaleManager.setLocale(this@MenuActivity, language)
        }
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPeriodSpinner()
        updateCalendarView()
    }

    private fun setupPeriodSpinner() {
        val periods = listOf("1 Тиждень", "2 Тижні", "1 Місяць")
        val periodValues = listOf(7, 14, 30)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.periodSpinner.adapter = adapter

        binding.periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPeriod = periodValues[position]
                updateCalendarView()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateCalendarView() {
        val dates = mutableListOf<Date>()
        val tempCalendar = calendar.clone() as Calendar

        val currentLocale =
            LocaleManager.getLanguage(this)?.let { Locale(it) } ?: Locale.getDefault()

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", currentLocale)

        for (i in 0 until selectedPeriod) {
            dates.add(tempCalendar.time)
            tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 5)

        binding.recyclerView.adapter = CalendarAdapter(dates, { selectedDate ->
            showMenuForDay(dateFormat.format(selectedDate))
        }, this)
    }


    private fun showMenuForDay(date: String) {
        val meals = listOf(
            Meal("Cніданок", listOf(Dish("Варені яйця", "Опис страви..."))),
            Meal("Перекус", listOf(Dish("Яблуко", "Опис страви..."))),
            Meal(
                "Обід",
                listOf(Dish("Борщ", "Опис страви..."), Dish("Канапки з тунцем", "Опис страви..."))
            ),
            Meal("Другий перекус", listOf(Dish("Грецький йогурт з бананом", "Опис страви..."))),
            Meal("Вечеря", listOf(Dish("Макарони з підливкою", "Опис страви...")))
        )

        val spannableText = SpannableStringBuilder()

        meals.forEach { meal ->
            spannableText.append("${meal.name}: ")

            meal.dishes.forEachIndexed { index, dish ->
                val start = spannableText.length
                spannableText.append(dish.dishName)
                if (index < meal.dishes.size - 1) {
                    spannableText.append(", ")
                }
                val end = spannableText.length

                spannableText.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        openDishDetails(dish)
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            spannableText.append("\n")
        }

        binding.menuDetails.text = spannableText
        binding.menuDetails.movementMethod = LinkMovementMethod.getInstance()
        binding.selectedDate.text = date
    }

    private fun openDishDetails(dish: Dish) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://test")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val html = response.body?.string()
                    Log.d("a", " $html")
                    val doc = Jsoup.parse(html!!)
                    val price = doc.select("meta[property=og:title]").attr("content")
                        .takeIf { it.contains("ціною від") }?.split("від")?.last()?.trim()
                    Log.d("a", " $price")
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@MenuActivity, DishDetailsActivity::class.java)
                        intent.putExtra("DISH_NAME", dish.dishName)
                        intent.putExtra("DISH_DESCRIPTION", dish.description)
                        intent.putExtra("DISH_PRICE", price)
                        startActivity(intent)
                    }
                } else {
                    // Handle the error if the response is not successful
                }
            }
        }
    }

}

