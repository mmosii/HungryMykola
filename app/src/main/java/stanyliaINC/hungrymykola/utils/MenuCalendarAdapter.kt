package stanyliaINC.hungrymykola.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import stanyliaINC.hungrymykola.R
import stanyliaINC.hungrymykola.dao.MealDao
import java.text.SimpleDateFormat
import java.util.*

class MenuCalendarAdapter(
    private var days: List<Date>,
    private val mealDao: MealDao,
    private val lifecycleOwner: LifecycleOwner,
    private val onDayClick: (Date) -> Unit,
    context: Context
) : RecyclerView.Adapter<MenuCalendarAdapter.DayViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd.MM", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("E", LocaleManager.getLanguage(context)?.let { Locale(it) } ?: Locale.getDefault())

    private var selectedDate: Date? = null

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

        @SuppressLint("SetTextI18n")
        fun bind(date: Date) {
            val dateText = dateFormat.format(date)
            val dayOfWeekText = dayOfWeekFormat.format(date)
            val dateString = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(date)

            lifecycleOwner.lifecycleScope.launch {
                val mealsByDate = mealDao.getMealsByDate(dateString)

                if (mealsByDate.isEmpty()) {
                    itemView.isVisible = false
                } else {
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                    itemView.isVisible = true
                }


                if (selectedDate == date && mealsByDate.isNotEmpty()) {
                    val colorFrom = (itemView.background as? ColorDrawable)?.color ?: Color.TRANSPARENT
                    val colorTo = if (selectedDate == date) Color.GREEN else Color.TRANSPARENT
                    ObjectAnimator.ofObject(itemView, "backgroundColor", ArgbEvaluator(), colorFrom, colorTo).setDuration(300).start()

                }
            }

            dateTextView.text = "$dateText\n$dayOfWeekText"

            itemView.setOnClickListener {
                selectedDate = date
                notifyDataSetChanged()
                onDayClick(date)
            }
        }
    }
}
