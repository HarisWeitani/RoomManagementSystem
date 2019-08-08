package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R

class NewsPagerAdapter(internal var newsList: List<News>, internal var context: Context) : PagerAdapter() {
    lateinit var tv_news_title: TextView
    lateinit var tv_news_content: TextView

    override fun getCount(): Int {
        return newsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.layout_news_pager, container, false)

        tv_news_title = itemView.findViewById(R.id.tv_news_title)
        tv_news_content = itemView.findViewById(R.id.tv_news_content)

        tv_news_title.text = newsList[position].title
        tv_news_content.text = newsList[position].content

        container.addView(itemView)

        return itemView
    }
}
