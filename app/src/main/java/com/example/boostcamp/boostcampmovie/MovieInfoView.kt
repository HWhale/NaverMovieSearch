package com.example.boostcamp.boostcampmovie

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import org.json.JSONArray

class MovieInfoView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // Adapter Class
    class MyAdapter(private val myDataset: JSONArray) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        private var mDataset : JSONArray = myDataset
        class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            internal val mTextView = view.findViewById(R.id.ViewHolderText) as TextView
            internal val mImageView = view.findViewById(R.id.ViewHolderImage) as ImageView

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
            val viewHolder = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_viewholder, parent, false) as View
            return MyViewHolder(viewHolder);
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = mDataset.getJSONObject(position)
            Log.v("Here", item.toString())
            var testTxt = item.getString("title") + '\n'
            testTxt += item.getString("director")
            testTxt += item.getString("actor")

            holder.mTextView.setText(testTxt)
            val imageRequest = ImageRequest(item.getString("image"),
                    Response.Listener {response ->
                        holder.mImageView.setImageBitmap(response)
                    },
                    300, 200, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                    Response.ErrorListener {

                    })

            AppSingleton.addMessage(imageRequest);
        }


        override fun getItemCount() = mDataset.length()
    }

    fun init(context: Context) {
        layoutManager = LinearLayoutManager(context)
    }

    fun updateData(dataSet: JSONArray) {
        adapter = MyAdapter(dataSet)
        this.invalidate()
    }
}