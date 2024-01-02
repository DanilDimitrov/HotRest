package com.girls.HotRest

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class preload : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val videoView = view.findViewById<VideoView>(R.id.videoView2)
        val continueButton = view.findViewById<AppCompatButton>(R.id.button2)// Найдите VideoView по его ID
        val terms = view.findViewById<TextView>(R.id.textView5)
        val policy = view.findViewById<TextView>(R.id.textView7)

        terms.paintFlags = terms.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        terms.setOnClickListener {
            val url = "https://reimageapp.ai/Terms_Of_Use-hotrest"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        policy.paintFlags = policy.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        policy.setOnClickListener {
            val url = "https://reimageapp.ai/hotrest_privacy-policy"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }


        val videoPath = "android.resource://" + requireActivity().packageName + "/" + R.raw.preload

        videoView.setVideoURI(Uri.parse(videoPath)) // Установите URI видео в VideoView

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
            videoView.start()
        }
        continueButton.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("isFragmentViewed", true)
            editor.apply()
            val proScreen = pro_screen()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(R.id.frameLayout, proScreen)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            preload().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}