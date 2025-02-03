package stanyliaINC.hungrymykola.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import stanyliaINC.hungrymykola.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class ShoppingListCalendarAdapter(
    context: Context,
    private val days: List<LocalDate>,
    private val onDateRangeSelected: (LocalDate, LocalDate) -> Unit
) : RecyclerView.Adapter<ShoppingListCalendarAdapter.CalendarViewHolder>() {

    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())
    private val dayOfWeekFormat = DateTimeFormatter.ofPattern("E", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.bind(date)

        holder.itemView.setOnClickListener {
            if (startDate == null || endDate != null) {
                startDate = date
                endDate = null
            } else {
                if (startDate == date) {
                    startDate = null
                    endDate = null
                } else if (date.isAfter(startDate)) {
                    endDate = date
                    onDateRangeSelected(startDate!!, endDate!!)
                } else if (date.isBefore(startDate)) {
                    endDate = date
                    startDate = null
                    endDate = null
                }
            }
            notifyDataSetChanged()
        }

        holder.itemView.setOnLongClickListener {
            if (startDate == null || endDate == null) {
                startDate = date
                endDate = date
                onDateRangeSelected(startDate!!, endDate!!)
            } else {
                startDate = null
                endDate = null
            }
            notifyDataSetChanged()
            true
        }
    }


    override fun getItemCount(): Int = days.size

    inner class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateText: TextView = view.findViewById(R.id.dateText)


        @SuppressLint("SetTextI18n")
        fun bind(date: LocalDate) {
            val dateFormatted = date.format(dateFormat)
            val dayOfWeekFormatted = date.format(dayOfWeekFormat)


            dateText.text = "$dateFormatted\n$dayOfWeekFormatted"

            itemView.setBackgroundColor(
                when {
                    date == startDate -> Color.GREEN
                    endDate != null && date.isAfter(startDate) && date.isBefore(endDate) -> Color.LTGRAY
                    date == endDate -> Color.GREEN
                    else -> Color.TRANSPARENT
                }
            )
        }
    }
}
