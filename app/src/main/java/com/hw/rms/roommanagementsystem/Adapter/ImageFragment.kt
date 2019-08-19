package com.hw.rms.roommanagementsystem.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hw.rms.roommanagementsystem.Helper.GlobalVal

import com.hw.rms.roommanagementsystem.R
import java.io.File

class ImageFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var imageName: String? = null
    private var imageUrl: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    lateinit var image_view : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            imageName = arguments!!.getString(ARG_PARAM1)
            imageUrl = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.fragment_image, container, false)
        image_view = v.findViewById(R.id.image_view)

        val myBitmap = BitmapFactory.decodeFile("$imageUrl/$imageName")
        image_view.setImageBitmap(myBitmap)

        return v
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        fun newInstance(param1: String, param2: String): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
