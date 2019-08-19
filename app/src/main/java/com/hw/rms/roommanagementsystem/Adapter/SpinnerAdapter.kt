package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.hw.rms.roommanagementsystem.Data.ResponseRoom
import com.hw.rms.roommanagementsystem.Helper.DAO

class SpinnerAdapter(internal val context: Context, resource: Int, private val responseRooms: List<ResponseRoom>) :
    ArrayAdapter<ResponseRoom>(context, resource, responseRooms) {

    override fun getCount(): Int {
        return responseRooms.size
    }

    override fun getItem(position: Int): ResponseRoom? {
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