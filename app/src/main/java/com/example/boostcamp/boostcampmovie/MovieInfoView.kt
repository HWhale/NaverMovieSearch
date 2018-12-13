package com.example.boostcamp.boostcampmovie

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONArray

class MovieInfoView : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // Adapter Class
    class MyAdapter(private val myDataset: JSONArray) :
            RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        var mDataset : JSONArray = myDataset
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
            val item = mDataset.getJSONObject(position)
            Log.v("Here", item.toString())
            var testTxt = item.getString("title") + '\n'
            testTxt += item.getString("director")
            testTxt += item.getString("actor")
            holder.mTextView.setText(testTxt)
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