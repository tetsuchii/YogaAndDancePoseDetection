package onlab.mlkit.tiktok

import android.content.Context
import android.content.Intent
import android.media.metrics.Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import onlab.mlkit.tiktok.databinding.ItemDanceBinding

class ListAdapter (
    var dances : List<Dance>,
    val mContext : Context,
    var listener : OnItemClickListener
        ) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDanceBinding.inflate(layoutInflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            danceName.text = dances[position].name
            danceDetail.text = dances[position].description

        }
    }

    override fun getItemCount(): Int {
        return dances.size
    }

    inner class ListViewHolder(val binding: ItemDanceBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var intent: Intent? = null
        var learnButton: Button = binding.learnSteps
        var practiceButton: Button = binding.practiceSteps
        init {
            learnButton.setOnClickListener {
                intent = Intent(mContext, CameraActivity::class.java)
                intent!!.type = "learn"
                mContext.startActivity(intent)
            }
            practiceButton.setOnClickListener {
                intent = Intent(mContext, CameraActivity::class.java)
                intent!!.type = "practice"
                mContext.startActivity(intent)
            }
        }

        override fun onClick(p0: View?) {
            listener.OnItemCLick()
        }
    }

    interface OnItemClickListener {
        fun OnItemCLick()
    }
}


