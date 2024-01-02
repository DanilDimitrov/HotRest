package com.girls.HotRest

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class style_adapter(val list: ArrayList<Model>, val typeUser:Boolean): RecyclerView.Adapter<style_adapter.ViewHolder>() {
    private var onItemClick: ((Model) -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val text: TextView = itemView.findViewById(R.id.textView24)
        val image = itemView.findViewById<ImageView>(R.id.imageView6)
        val free = itemView.findViewById<AppCompatButton>(R.id.free)

        val pro = itemView.findViewById<AppCompatButton>(R.id.pro)




        fun bind(style: Model) {
            Log.i("typeUser", typeUser.toString())
            if(typeUser == false && style.type == "free"){
                free.visibility = View.VISIBLE
                pro.visibility = View.INVISIBLE
            }else if(typeUser == false && style.type == null){
                free.visibility = View.INVISIBLE
                pro.visibility = View.VISIBLE
            }else{
                free.visibility = View.VISIBLE
                pro.visibility = View.INVISIBLE
            }

            Glide.with(image)
                .asDrawable()
                .load(style.imagePath)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(image)


            itemView.setOnClickListener {
                val prompt = list[adapterPosition]
                Log.i("prompt", prompt.toString())
                onItemClick?.invoke(prompt)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.style_adapter, parent, false)
        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener: (Model) -> Unit) {
        onItemClick = listener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val style = list[position]
        holder.bind(style)
    }
}
