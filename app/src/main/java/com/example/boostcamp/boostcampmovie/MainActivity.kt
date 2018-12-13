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

    internal lateinit var mRequestQueue: RequestQueue
    internal lateinit var mRecyclerView: RecyclerView
    internal lateinit var mViewAdapter: RecyclerView.Adapter<*>
    internal lateinit var mViewManager: RecyclerView.LayoutManager

    interface ReturnCallback {
        fun setValue(`object`: Any)
    }

    class MyAdapter(private val myDataset: Array<String>) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        var mDataset : Array<String> = myDataset
        class MyViewHolder(val textView: View) : RecyclerView.ViewHolder(textView) {
            var mTextView = textView.findViewById(R.id.ViewHolderText) as TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
            Log.v("Item", "hiaaaa")
            val textView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_viewholder, parent, false) as View
            return MyViewHolder(textView);
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.mTextView.setText(mDataset.get(position))
        }


        override fun getItemCount() = mDataset.size

        fun setItem() {
            mDataset[0] = "Fixed"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.v("Value", "Test")

        val myDataset = arrayOf("hi", "hello", "recycling", "template", "testing", "etc")

        mRequestQueue = Volley.newRequestQueue(this)

        mViewManager = LinearLayoutManager(this)
        mViewAdapter = MyAdapter(myDataset)

        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            //setHasFixedSize(true)
            layoutManager = mViewManager
            adapter = mViewAdapter
        }

        myDataset.set(1, "modified")
        mViewAdapter.notifyItemChanged(1);

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

        val bitmapArr = arrayOfNulls<Bitmap>(10)
        //val textView = findViewById<View>(R.id.Text1) as TextView

        val url = "https://openapi.naver.com/v1/search/movie.json?query=superman"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : JsonObjectRequest(Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    //textView.setText("Response is: ");
                    try {
                        //textView.text = response.get("items").toString()
                        val itemArr = response.get("items") as JSONArray
                        for (i in 0 .. itemArr.length()) {
                            val item = itemArr.get(i) as JSONObject
                            val urlImage = item.get("image").toString()
                            val imageRequest = ImageRequest(urlImage,
                                    Response.Listener { Log.i("image", "got image") }, 300, 200, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                                    Response.ErrorListener { error -> Log.e("Image", error.toString()) })
                            mRequestQueue.add(imageRequest)

                            Log.i("item", item.get("image").toString())
                        }
                    } catch (e: JSONException) {
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Volley", error.toString())
                    //textView.text = "That didn't work!"
                }
        ) {
            //@Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("X-Naver-Client-Id", "9XnUQAJXupcY1g8SBt74");
                headers.put("X-Naver-Client-Secret", "JpEW74dmIO");
                return headers
            }
        }
        mRequestQueue.add(stringRequest)
    }

    private fun getImageRequest(url: String): Bitmap? {
        val imageRequest = ImageRequest(url,
                Response.Listener { }, 300, 200, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                Response.ErrorListener { })
        mRequestQueue.add(imageRequest)
        return null
    }
}
