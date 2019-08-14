package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.viewpager.widget.PagerAdapter
import com.hw.rms.roommanagementsystem.Model.ImageVideo
import com.hw.rms.roommanagementsystem.Model.News
import com.hw.rms.roommanagementsystem.R
import java.io.File

class ImageVideoPagerAdapter(internal var imageVideoList: List<ImageVideo>, internal var filePath : String,internal var context: Context) : PagerAdapter() {

    lateinit var iv_image: ImageView
    lateinit var vv_video: VideoView


    override fun getCount(): Int {
        return imageVideoList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.layout_imagevideo_pager, container, false)

        iv_image = itemView.findViewById(R.id.iv_image)
        vv_video = itemView.findViewById(R.id.vv_video)

//        if( !imageVideoList[position].imageTitle.equals(null) ){
//            iv_image
//        }

        vv_video.setVideoURI(Uri.parse(filePath))
        vv_video.start()

//        iv_image.setImage = newsListLeft[position].title
//        tv_news_content_left.text = newsListLeft[position].content

        container.addView(itemView)

        return itemView
    }
}
