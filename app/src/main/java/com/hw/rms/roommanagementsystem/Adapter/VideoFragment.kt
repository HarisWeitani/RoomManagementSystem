package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import com.hw.rms.roommanagementsystem.Activity.MainActivity
import com.hw.rms.roommanagementsystem.Helper.GlobalVal

import com.hw.rms.roommanagementsystem.R
import java.lang.Exception

class VideoFragment : Fragment() {

    private var videoUrl: String? = null
    private var videoName: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    lateinit var video_view : VideoView
    var isStarted = false
    var isViewVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            videoName = arguments!!.getString(ARG_PARAM1)
            videoUrl = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =inflater.inflate(R.layout.fragment_video, container, false)
        video_view = v.findViewById(R.id.video_view)
        video_view.setOnCompletionListener {
            GlobalVal.isVideoStarted = false
            MainActivity.instance.setNextImageVideoPager(
                isVideoComplete = true,
                isFromImage = false
            )
        }
        return v
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onStart() {
        super.onStart()
        isStarted = true
        viewDidAppear()
    }

    override fun onStop() {
        super.onStop()
        isStarted = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if ( isVisibleToUser ) {
            isViewVisible = true
            viewDidAppear()
        }
        else {
            isViewVisible = false
            try {
                video_view.stopPlayback()
            }catch ( e : Exception ){}
        }
        Log.d("ahsiap", "Is Video Visible To User $isVisibleToUser")
    }

    fun viewDidAppear(){
        if(isStarted && isViewVisible){
            try {
                video_view.setVideoPath("$videoUrl/$videoName")
                video_view.start()
                GlobalVal.isVideoStarted = true
            }catch (e : Exception){}
        }else{
            GlobalVal.isVideoStarted = false
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): VideoFragment {
            val fragment = VideoFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}