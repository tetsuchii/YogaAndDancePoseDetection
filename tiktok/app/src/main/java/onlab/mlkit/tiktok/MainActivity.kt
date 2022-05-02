package onlab.mlkit.tiktok


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.*
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import onlab.mlkit.tiktok.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ListAdapter.OnItemClickListener {

    private lateinit var viewBinding: ActivityMainBinding
    var dancesList = mutableListOf(
        Dance("Running man","The famous shuffle move. Dance like Redfoo in Party Rock Anthem!"),
        Dance("Happy dance","This dance makes everyone happy around you!")
    )
    val adapter = ListAdapter(dancesList,this,this)


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

    override fun OnItemCLick() {
        adapter.notifyDataSetChanged()
    }


}