package onlab.mlkit.tiktok.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onlab.mlkit.tiktok.databinding.ItemPoseBinding

class PoseListAdapter(
    var poses: List<Pose>,
    val mContext: Context,
    var listener: OnItemClickListener,
    var resources: Resources,
    var packageName: String
) : RecyclerView.Adapter<PoseListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPoseBinding.inflate(layoutInflater, parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            poseName.text = poses[position].name
            poseDetail.text = poses[position].description
            val uri = "@drawable/${poses[position].name.filter { !it.isWhitespace() }.lowercase()}"
            val imageResource = resources.getIdentifier(uri, null, packageName)
        val res = resources.getDrawable(imageResource)
            poseImage.setImageDrawable(res)
        }
    }

    override fun getItemCount(): Int {
        return poses.size
    }

    inner class ListViewHolder(val binding: ItemPoseBinding) :
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


