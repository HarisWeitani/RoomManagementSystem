package com.hw.rms.roommanagementsystem.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hw.rms.roommanagementsystem.Data.DataScheduleByDate
import com.hw.rms.roommanagementsystem.Data.ScheduleData
import com.hw.rms.roommanagementsystem.R
import kotlinx.android.synthetic.main.list_schedule_item.view.*

class ScheduleAdapter( private val scheduleDatas : List<DataScheduleByDate> ) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        return ScheduleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_schedule_item, parent,false))
    }

    override fun getItemCount(): Int = scheduleDatas.size


    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(scheduleDatas[position])
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_time_schedule = itemView.tv_time_schedule
        private val tv_time_content_schedule = itemView.tv_time_content_schedule

        fun bind(data : DataScheduleByDate){
            tv_time_schedule.text = "${data.start_dateTime} - ${data.end_dateTime}"
            tv_time_content_schedule.text = data.summary
        }
    }

}