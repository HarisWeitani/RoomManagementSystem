package com.hw.rms.roommanagementsystem.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hw.rms.roommanagementsystem.Model.ImageVideo

class ImageVideoAdapter(fm: FragmentManager?, var filePath: String?, var imageVideo: MutableList<ImageVideo>) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        if ( imageVideo[position].imageName != null )
            return ImageFragment.newInstance("","")
        else if ( imageVideo[position].videoName != null )
            return VideoFragment.newInstance("","")
        else
            return ErrorFragment.newInstance("","")

    }

    override fun getCount(): Int {
        return imageVideo.size
    }

}