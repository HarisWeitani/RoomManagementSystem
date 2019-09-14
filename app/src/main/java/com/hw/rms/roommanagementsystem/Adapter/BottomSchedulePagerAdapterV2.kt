package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.hw.rms.roommanagementsystem.Data.DataGetNextMeeting
import com.hw.rms.roommanagementsystem.R

class BottomSchedulePagerAdapterV2(internal var botSchedLeft: List<DataGetNextMeeting>, internal var botSchedRight: List<DataGetNextMeeting>, internal var context: Context) : PagerAdapter() {

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

        var leftStartTime = botSchedLeft[position].start_dateTime
        var leftEndTime = botSchedLeft[position].end_dateTime

        if( leftStartTime!!.length > 5 && leftEndTime!!.length > 5 ){
            leftStartTime = leftStartTime.substring(0,5)
            leftEndTime = leftEndTime.substring(0,5)
        }

        tv_bottom_schedule_title_left.text =
            "Next Meeting $leftStartTime - $leftEndTime"
        tv_bottom_schedule_content_left.text =
            "${botSchedLeft[position].summary} By ${botSchedLeft[position].creator}"

        var rightStartTime = botSchedRight[position].start_dateTime
        var rightEndTime = botSchedRight[position].end_dateTime

        if( rightStartTime!!.length > 5 && rightEndTime!!.length > 5 ){
            rightStartTime = rightStartTime.substring(0,5)
            rightEndTime = rightEndTime.substring(0,5)
        }

        tv_bottom_schedule_title_right.text =
            "Next Meeting $rightStartTime - $rightEndTime"
        tv_bottom_schedule_content_right.text =
            "${botSchedRight[position].summary} By ${botSchedRight[position].creator}"

        container.addView(itemView)

        return itemView
    }
}
