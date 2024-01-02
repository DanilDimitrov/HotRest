package com.girls.HotRest

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class galleryAdapter(val images: ArrayList<String>): RecyclerView.Adapter<galleryAdapter.PhotoHolder>() {
    private var onItemClick: ((String) -> Unit)? = null

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.photo)

        fun bind(imageThis: String) {
            Glide.with(image)
                .asDrawable()
                .load(imageThis)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(image)


            itemView.setOnClickListener {
                val prompt = images[adapterPosition]
                Log.i("prompt", prompt.toString())
                onItemClick?.invoke(prompt)
            }

        }
    }
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): galleryAdapter.PhotoHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_adapter, parent, false)
        return PhotoHolder(view)
    }

    override fun onBindViewHolder(holder: galleryAdapter.PhotoHolder, position: Int) {
        val style = images[position]
        holder.bind(style)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}