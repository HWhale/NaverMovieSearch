package com.example.boostcamp.boostcampmovie

import android.Manifest
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

const val NAVER_CLIENT_ID = "9XnUQAJXupcY1g8SBt74"
const val NAVER_CLIENT_SECRET = "JpEW74dmIO"

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
        mMovieInfoView.init(this)

        mSearchButton.setOnClickListener { view ->
            getNaverMovie(mQueryEdit.text.toString());
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

    private fun getNaverMovie(keyword: String) {
        val url = "https://openapi.naver.com/v1/search/movie.json?query="+keyword
        val stringRequest = object : JsonObjectRequest(Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    try {
                        val itemArr = response.get("items") as JSONArray
                        mMovieInfoView.updateData(itemArr)
                    } catch (e: JSONException) {
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Volley", error.toString())
                }
        ) {
            //@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("X-Naver-Client-Id", NAVER_CLIENT_ID);
                headers.put("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
                return headers
            }
        }
        AppSingleton.addMessage(stringRequest)
    }
}