package com.hw.rms.roommanagementsystem.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.hw.rms.roommanagementsystem.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.previous
import kotlinx.android.synthetic.main.activity_schedule.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*
class ScheduleActivity : AppCompatActivity() {

    lateinit var calendar_view : CalendarView
    lateinit var btn_prev_month : Button
    lateinit var btn_next_month : Button

    private var selectedDate: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        actionBar?.hide()
        AndroidThreeTen.init(this)

        calendar_view = findViewById(R.id.calendar_view)
        btn_prev_month = findViewById(R.id.btn_prev_month)
        btn_next_month = findViewById(R.id.btn_next_month)

        initCalendarView()


    }

    fun initCalendarView(){
        val daysOfWeek = daysOfWeekFromLocale()

        val currentMonth = YearMonth.now()

        calendar_view.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first())
        calendar_view.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val tv_date = view.findViewById<TextView>(R.id.tv_calendar_day)
            val layout = view.findViewById<FrameLayout>(R.id.calendar_day_layout)

            val tv_calendar_day = view.findViewById<TextView>(R.id.tv_calendar_day)
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendar_view.notifyDateChanged(day.date)
                            oldDate?.let { calendar_view.notifyDateChanged(it) }
//                            updateAdapterForDate(day.date)
                        }
                    }
                }
            }
        }
        calendar_view.dayBinder = object : DayBinder<DayViewContainer>{
            override fun create(view: View) =  DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val tv_date = container.tv_date
                val layout = container.layout

                tv_date.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH) {
                    tv_date.setTextColor(ContextCompat.getColor(applicationContext,R.color.grey))
                    layout.setBackgroundResource(if (selectedDate == day.date) R.drawable.date_selected_bg else 0)

//                    val flights = flights[day.date]
//                    if (flights != null) {
//                        if (flights.count() == 1) {
//                            flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                        } else {
//                            flightTopView.setBackgroundColor(view.context.getColorCompat(flights[0].color))
//                            flightBottomView.setBackgroundColor(view.context.getColorCompat(flights[1].color))
//                        }
//                    }
                } else {
                    tv_date.setTextColor(ContextCompat.getColor(applicationContext,R.color.grey_light))
                    layout.background = null
                }
            }
        }
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.findViewById<LinearLayout>(R.id.legend_layout)
        }
        calendar_view.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.take(3)
//                        tv.setTextColorRes(R.color.example_5_text_grey)
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    }
                    month.yearMonth
                }
            }
        }

        calendar_view.monthScrollListener = { month ->
            val title = "${monthTitleFormatter.format(month.yearMonth)} ${month.yearMonth.year}"
            exFiveMonthYearText.text = title

//            selectedDate?.let {
//                // Clear selection if we scroll to a new month.
//                selectedDate = null
//                calendar_view.notifyDateChanged(it)
//                updateAdapterForDate(null)
//            }
        }

        btn_prev_month.setOnClickListener {
            calendar_view.findFirstVisibleMonth()?.let {
                calendar_view.smoothScrollToMonth(it.yearMonth.previous)
            }
        }

        btn_next_month.setOnClickListener {
            calendar_view.findFirstVisibleMonth()?.let {
                calendar_view.smoothScrollToMonth(it.yearMonth.next)
            }
        }

    }


    fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }
}
