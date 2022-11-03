package onlab.mlkit.tiktok


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import onlab.mlkit.tiktok.data.DanceListAdapter
import onlab.mlkit.tiktok.data.Details
import onlab.mlkit.tiktok.data.Type
import onlab.mlkit.tiktok.databinding.ActivityMainBinding
import java.text.FieldPosition


class MainActivity : AppCompatActivity(), DanceListAdapter.OnItemClickListener {

    private lateinit var viewBinding: ActivityMainBinding
    var dancesList = mutableListOf(
        Details("Running man","The famous shuffle move. Dance like Redfoo in Party Rock Anthem!","ize",
            Type.DANCE),
        Details("Tree","One of the most famous yoga pose. You definitely have to try it!","ize",Type.YOGA),
        Details("Warrior2", "Something funny and catchy about the pose.","ize",Type.YOGA),
        Details("Goddess", "Lorem ipsum idk...","ize",Type.YOGA)

    )
    val adapter = DanceListAdapter(dancesList,this,this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val animDrawable = viewBinding.rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()
        viewBinding.rvDances.adapter = adapter
        viewBinding.rvDances.layoutManager = LinearLayoutManager(this)


    }


    override fun OnItemClick(position: Int) {
        displayModePickerDialog(position)
        adapter.notifyDataSetChanged()    }

    private fun displayModePickerDialog(position: Int) {
        var popupDialog = Dialog(this)
        popupDialog.setCancelable(true)
        popupDialog.setContentView(R.layout.mode_picker_dialog)
        popupDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var image=popupDialog.findViewById<ImageView>(R.id.move_image)
        var name=popupDialog.findViewById<TextView>(R.id.move_name)
        var detail=popupDialog.findViewById<TextView>(R.id.move_detail)
        var moreDetail=popupDialog.findViewById<TextView>(R.id.move_more_detail)
        var learnButton:Button=popupDialog.findViewById(R.id.learn)
        var practiceButton:Button=popupDialog.findViewById(R.id.practice)

        var codeName = when(dancesList[position].type){
            Type.YOGA -> "yoga "
            Type.DANCE -> "dance "
        }
        codeName+=dancesList[position].name.toString()
        learnButton.setOnClickListener {
            intent = Intent(this, CameraActivity::class.java)

        intent!!.type = "learn $codeName"
        this.startActivity(intent)
        }

        practiceButton.setOnClickListener{
            intent = Intent(this, CameraActivity::class.java)
        intent!!.type = "practice $codeName"
        this.startActivity(intent)
        }

        image.setImageResource(R.drawable.first)
        name.text = dancesList[position].name
        detail.text=dancesList[position].description

        popupDialog.show()
    }


}