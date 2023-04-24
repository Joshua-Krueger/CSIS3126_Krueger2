package com.example.csis_2023

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.bumptech.glide.Glide
import android.widget.AbsListView.LayoutParams


class ImageAdapter(private val context: Context, private val imageUrls: List<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun getItem(position: Int): Any {
        return imageUrls[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(context)

        // Set layout parameters for image
        val layoutParams = LayoutParams(350, 350)
        imageView.layoutParams = layoutParams

        // Load image using Glide
        Glide.with(context)
            .load(imageUrls[position])
            .centerCrop()
            .into(imageView)

        return imageView
    }
}
