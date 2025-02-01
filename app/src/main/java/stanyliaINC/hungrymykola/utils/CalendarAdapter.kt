package stanyliaINC.hungrymykola.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.MealDao
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private var days: List<Date>,
    private val mealDao: MealDao,
    private val lifecycleOwner: LifecycleOwner,
    private val onDayClick: (Date) -> Unit,
    context: Context
) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("E", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]
        holder.bind(date)
    }

    override fun getItemCount(): Int = days.size

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.text_day)

        fun bind(date: Date) {
            val dateText = dateFormat.format(date)
            val dayOfWeekText = dayOfWeekFormat.format(date)

            val dateString = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(date)

            lifecycleOwner.lifecycleScope.launch {
                val mealsByDate = mealDao.getMealsByDate(dateString)
                if (mealsByDate.isEmpty()) {
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.red))
                } else {
                    itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green))
                }
            }

            dateTextView.text = "$dateText\n$dayOfWeekText"
            itemView.setOnClickListener { onDayClick(date) }
        }
    }
}
