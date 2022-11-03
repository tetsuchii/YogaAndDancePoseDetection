package onlab.mlkit.tiktok.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onlab.mlkit.tiktok.R
import onlab.mlkit.tiktok.databinding.ItemDanceBinding

class DanceListAdapter (
    var dances : List<Details>,
    val mContext : Context,
    var listener : OnItemClickListener
        ) : RecyclerView.Adapter<DanceListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDanceBinding.inflate(layoutInflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            danceName.text = dances[position].name
            danceDetail.text = dances[position].description
            danceImage.setImageResource(R.drawable.fifth)
        }
    }

    override fun getItemCount(): Int {
        return dances.size
    }

    inner class ListViewHolder(val binding: ItemDanceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }
        @SuppressLint("SuspiciousIndentation")
        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION)
            listener.OnItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun OnItemClick(position: Int)
    }
}


