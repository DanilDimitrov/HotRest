package com.girls.HotRest

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class inspire_me_adapter(val list: ArrayList<String>): RecyclerView.Adapter<inspire_me_adapter.ViewHolder>() {
    private var onItemClick: ((String) -> Unit)? = null
    private var selectedItem = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.textView24)
        val cardView = itemView.findViewById<CardView>(R.id.card)

        fun bind(style: String) {
            text.text = style
            if (adapterPosition == selectedItem) {
                cardView.setBackgroundResource(R.drawable.inspire_me)
            } else {
                cardView.setBackgroundResource(R.drawable.inspire_select)
            }

            itemView.setOnClickListener {
                val previousSelected = selectedItem
                selectedItem = adapterPosition
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedItem)
                val prompt = list[adapterPosition]
                onItemClick?.invoke(prompt)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inspire_adapter, parent, false)
        return ViewHolder(view)
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
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
