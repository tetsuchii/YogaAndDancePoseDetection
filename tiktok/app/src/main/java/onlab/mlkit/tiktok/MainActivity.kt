package onlab.mlkit.tiktok


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import onlab.mlkit.tiktok.data.Pose
import onlab.mlkit.tiktok.data.PoseDatabase
import onlab.mlkit.tiktok.data.PoseListAdapter
import onlab.mlkit.tiktok.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), PoseListAdapter.OnItemClickListener {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var database : PoseDatabase
    private  lateinit var adapter : PoseListAdapter
    private lateinit var auth: FirebaseAuth

    private lateinit var profile : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val animDrawable = viewBinding.rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        profile=findViewById(R.id.profile)

        profile.setOnClickListener {
            displayLogoutDialog()
        }

        auth= Firebase.auth

        database= PoseDatabase.getDatabase(applicationContext)
        //println(database.poseDao().getAll())
        adapter = PoseListAdapter(database.poseDao().getAll(),applicationContext, this, resources,packageName)
        viewBinding.rvDances.adapter = adapter
        viewBinding.rvDances.layoutManager = LinearLayoutManager(this)


    }

    private fun displayLogoutDialog() {
        var logoutDialog = Dialog(this)
        logoutDialog.setCancelable(true)
        logoutDialog.setContentView(R.layout.logout_dialog)
        logoutDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var button=logoutDialog.findViewById<ImageView>(R.id.logout)
        var cancel=logoutDialog.findViewById<TextView>(R.id.cancel)

        button.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,LoginActivity::class.java))
        }
        cancel.setOnClickListener {
            logoutDialog.cancel()
        }

        logoutDialog.show()
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
        }
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

        var codeName = when(database.poseDao().getAll()[position].type){
            Pose.Type.YOGA -> "yoga "
            Pose.Type.DANCE -> "dance "
            else -> {return}
        }
        codeName+=database.poseDao().getAll()[position].name.toString()
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
        val dataObject=database.poseDao().getAll()[position]
        name.text = dataObject.name
        detail.text=dataObject.description
        moreDetail.text=dataObject.detailedDescription
        val uri = "@drawable/${dataObject.name.filter { !it.isWhitespace() }.lowercase()}"
        val imageResource = resources.getIdentifier(uri, null, packageName)
        val res = resources.getDrawable(imageResource)
        image.setImageDrawable(res)

        popupDialog.show()
    }


}