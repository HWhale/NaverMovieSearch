package com.example.boostcamp.boostcampmovie

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONException
import java.net.URLEncoder
import java.util.HashMap

class MovieInfoView : RecyclerView {
    private var mKeyword: String? = null
    private var mIsLoading: Boolean = false
    private var mHasMore: Boolean = true
    private var mTotalSize: Int = -1
    private var mExpectedSize: Int = 0
    private val LOAD_SIZE: Int = 10
    private var mOpenLinkActivity : ((link: String) -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val DOWNWARD_DIR = 1
        if (canScrollVertically(DOWNWARD_DIR)) {
            return
        }
        if (!mIsLoading) {
            mIsLoading = true
            addMovie()
            mIsLoading = false
        }
    }

    // Adapter Class
    class MyAdapter(dataSet: JSONArray, openLinkActivity: ((link: String) -> Unit)?) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        internal val mDataset : JSONArray = dataSet
        internal var mImageList : MutableList<Bitmap?> = mutableListOf<Bitmap?>()
        internal var mOpenLinkActivity : ((link: String) -> Unit)? = openLinkActivity

        class MyViewHolder(val holder: View) : RecyclerView.ViewHolder(holder) {
            internal val mTextView = holder.findViewById(R.id.TitleText) as TextView

            internal val mTitleTextView = holder.findViewById(R.id.TitleText) as TextView
            internal val mYearTextView = holder.findViewById(R.id.YearText) as TextView
            internal val mScoreTextView = holder.findViewById(R.id.ScoreText) as TextView
            internal val mActorTextView = holder.findViewById(R.id.ActorText) as TextView
            internal val mDirectorTextView = holder.findViewById(R.id.DirectorText) as TextView

            internal val mImageView = holder.findViewById(R.id.ViewHolderImage) as ImageView

            internal val mLayout = holder.findViewById(R.id.HolderLayout) as View
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_viewholder, parent, false) as View

            return MyViewHolder(viewHolder);
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = mDataset.getJSONObject(position)

            holder.mTitleTextView.setText(Html.fromHtml(item.getString("title")))
            holder.mYearTextView.setText(item.getString("pubDate"))
            holder.mScoreTextView.setText(item.getString("userRating"))
            holder.mDirectorTextView.setText(item.getString("director"))
            holder.mActorTextView.setText(item.getString("actor"))

            val link = item.getString("link")
            holder.mLayout.setOnClickListener {
                mOpenLinkActivity!!.invoke(link)
            }

            // if an image exist, load it instead of calling api
            if (position < mImageList.size ) {
                holder.mImageView.setImageBitmap(mImageList[position])
                return
            }

            val imageUrl = item.getString("image")
            if (imageUrl.equals("")) {
                mImageList.add(mImageList.size, null)
                holder.mImageView.setImageBitmap(null)
                return
            }
            else {
                val imageRequest = ImageRequest(imageUrl,
                        Response.Listener {response ->
                            mImageList.add(mImageList.size, response)
                            holder.mImageView.setImageBitmap(response)
                        },
                        300, 200, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                        Response.ErrorListener {
                        })
                AppSingleton.addMessage(imageRequest);
            }
        }

        override fun getItemCount() = mDataset.length()
    }

    fun init(context: Context, openLinkActivity: (link: String) -> Unit) {
        layoutManager = LinearLayoutManager(context)
        mOpenLinkActivity = openLinkActivity
    }

    fun setKeyword(keyword: String) {
        mKeyword = keyword
        adapter = MyAdapter(JSONArray(), mOpenLinkActivity)
        mHasMore = true
        mTotalSize = -1
        mExpectedSize = 0
        addMovie()
    }

    fun addMovie() {
        if (!mHasMore) {
            return
        }
        val myAdapter = adapter as MyAdapter
        val itemSize = myAdapter.mDataset.length()
        var encodedKeyword = URLEncoder.encode(mKeyword, "utf-8")
        var url = "https://openapi.naver.com/v1/search/movie.json?query=${encodedKeyword}"
        url += "&display=${LOAD_SIZE}"
        url += "&start=${1 + itemSize}"

        val stringRequest = object : JsonObjectRequest(Request.Method.GET,
                url, null,
                Response.Listener { response ->
                    try {
                        val itemArr = response.get("items") as JSONArray
                        val totalSize = response.getInt("total")
                        if (mTotalSize == -1) {
                            mTotalSize = totalSize
                        }

                        updateData(itemArr)

                        if (itemSize >= totalSize)  {
                            mHasMore = false
                        }
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

    fun updateData(newDataset: JSONArray) {
        val myAdapter = adapter as MyAdapter
        val prevItemSize = myAdapter.mDataset.length()
        for(i in 0 .. newDataset.length() - 1) {
            myAdapter.mDataset.put(newDataset.get(i))
            Log.v("New Data", newDataset.getJSONObject(i).get("image").toString())
        }
        myAdapter.notifyItemRangeInserted(prevItemSize, newDataset.length())
    }
}