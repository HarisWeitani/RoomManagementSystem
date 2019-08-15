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
import android.graphics.BitmapFactory
import android.graphics.Bitmap



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

//        if( !imageVideoList[position].imageName.equals(null) ){
//            iv_image
//        }
//        if(position == 0) {
//            vv_video.setVideoURI(Uri.parse("$filePath/video.mp4"))
//            vv_video.start()
//        }else{
//            val imgFile = File("$filePath/image.png")
//
//            if (imgFile.exists()) {
//
//                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//
//                iv_image.setImageBitmap(myBitmap)
//
//            }
////            iv_image.setImageResource(R.drawable.elephant)
//        }

//        val imgFile = File("$filePath/pic.jpg")
//
//        iv_image.setImageURI(Uri.fromFile(imgFile.absoluteFile))
//
//        if (imgFile.exists()) {
//
//            iv_image.setImageBitmap(BitmapFactory.decodeFile(imgFile.absolutePath))
//
//        }

        iv_image.setImageResource(R.drawable.elephant)

//        iv_image.setImage = newsListLeft[position].title
//        tv_news_content_left.text = newsListLeft[position].content

        container.addView(itemView)

        return itemView
    }
}
