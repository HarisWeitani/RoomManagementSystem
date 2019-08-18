package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.hw.rms.roommanagementsystem.Data.DataGetNextMeeting
import com.hw.rms.roommanagementsystem.R

class BottomSchedulePagerAdapter(internal var botSchedLeft: List<DataGetNextMeeting>, internal var botSchedRight: List<DataGetNextMeeting>, internal var context: Context) : PagerAdapter() {

    lateinit var tv_bottom_schedule_title_left: TextView
    lateinit var tv_bottom_schedule_title_right: TextView

    lateinit var tv_bottom_schedule_content_left: TextView
    lateinit var tv_bottom_schedule_content_right: TextView

    override fun getCount(): Int {
        return botSchedLeft.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.layout_bottom_schedule_pager, container, false)

        tv_bottom_schedule_title_left = itemView.findViewById(R.id.tv_bottom_schedule_title_left)
        tv_bottom_schedule_content_left = itemView.findViewById(R.id.tv_bottom_schedule_content_left)

        tv_bottom_schedule_title_right = itemView.findViewById(R.id.tv_bottom_schedule_title_right)
        tv_bottom_schedule_content_right = itemView.findViewById(R.id.tv_bottom_schedule_content_right)

        tv_bottom_schedule_title_left.text =
            "Next Meeting " + botSchedLeft[position].booking_time_start + " - " + botSchedLeft[position].booking_time_end
        tv_bottom_schedule_content_left.text = botSchedLeft[position].meeting_title

        tv_bottom_schedule_title_right.text =
            "Next Meeting " + botSchedRight[position].booking_time_start + " - " + botSchedRight[position].booking_time_end
        tv_bottom_schedule_content_right.text = botSchedRight[position].meeting_title

        container.addView(itemView)

        return itemView
    }
}
