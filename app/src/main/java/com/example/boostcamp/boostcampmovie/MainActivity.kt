package com.example.boostcamp.boostcampmovie

import android.Manifest
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap



class MainActivity : AppCompatActivity() {
    private lateinit var mMovieInfoView: MovieInfoView
    private lateinit var mQueryEdit: EditText
    private lateinit var mSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppSingleton.initRequestQueue(this)

        initMembers()
        getNetworkPermission()
    }

    private fun initMembers() {
        mMovieInfoView = findViewById(R.id.recyclerView)
        mSearchButton = findViewById(R.id.buttonSearch)
        mQueryEdit = findViewById(R.id.editKeyword)
        mMovieInfoView.init(this, this::openLinkActivity)

        mSearchButton.setOnClickListener {
            mMovieInfoView.setKeyword(mQueryEdit.text.toString())
            mMovieInfoView.addMovie()
        }
    }

    private fun getNetworkPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                10)

        val permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("Permission", "Success")
        } else {
            Log.i("Permission", "Failed")
        }
    }

    internal fun openLinkActivity(link: String) {
        val intent = Intent()
        intent.setAction(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, link)
        startActivity(intent)
    }
}