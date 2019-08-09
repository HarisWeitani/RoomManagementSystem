package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R

class NewsPagerAdapter(internal var newsListLeft: List<News>, internal var newsListRight: List<News> , internal var context: Context) : PagerAdapter() {

    lateinit var tv_news_title_left: TextView
    lateinit var tv_news_title_right: TextView

    lateinit var tv_news_content_left: TextView
    lateinit var tv_news_content_right: TextView

    override fun getCount(): Int {
        return newsListLeft.size
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

        tv_news_title_left = itemView.findViewById(R.id.tv_news_title_left)
        tv_news_content_left = itemView.findViewById(R.id.tv_news_content_left)

        tv_news_title_right = itemView.findViewById(R.id.tv_news_title_right)
        tv_news_content_right = itemView.findViewById(R.id.tv_news_content_right)

        tv_news_title_left.text = newsListLeft[position].title
        tv_news_content_left.text = newsListLeft[position].content

        tv_news_title_right.text = newsListRight[position].title
        tv_news_content_right.text = newsListRight[position].content

        container.addView(itemView)

        return itemView
    }
}
