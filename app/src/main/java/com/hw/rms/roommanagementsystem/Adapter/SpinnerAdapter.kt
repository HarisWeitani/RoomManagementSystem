package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.hw.rms.roommanagementsystem.Data.DataGetAllRooms

class SpinnerAdapter(internal val context: Context, resource: Int, private val responseRooms: List<DataGetAllRooms>) :
    ArrayAdapter<DataGetAllRooms>(context, resource, responseRooms) {

    override fun getCount(): Int {
        return responseRooms.size
    }

    override fun getItem(position: Int): DataGetAllRooms? {
        return responseRooms[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.setTextColor(Color.WHITE)
        textView.text = responseRooms[position].room_name

        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(position, convertView, parent) as TextView
        textView.setTextColor(Color.WHITE)
        textView.text = responseRooms[position].room_name

        return textView
    }
}
