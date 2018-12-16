package com.example.boostcamp.boostcampmovie

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

const val NAVER_CLIENT_ID = "9XnUQAJXupcY1g8SBt74"
const val NAVER_CLIENT_SECRET = "JpEW74dmIO"

object AppSingleton {
    lateinit var sRequestQueue : RequestQueue

    fun initRequestQueue(context: Context) {
        sRequestQueue = Volley.newRequestQueue(context)
    }

    fun addMessage(request: Request<*>) {
        sRequestQueue.add(request)
    }
}