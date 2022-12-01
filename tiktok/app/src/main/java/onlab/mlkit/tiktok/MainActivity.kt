package onlab.mlkit.tiktok


import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import onlab.mlkit.tiktok.data.Pose
import onlab.mlkit.tiktok.data.PoseDatabase
import onlab.mlkit.tiktok.data.PoseListAdapter
import onlab.mlkit.tiktok.data.User
import onlab.mlkit.tiktok.databinding.ActivityMainBinding


class MainActivity : DrawerBaseActivity(), PoseListAdapter.OnItemClickListener {

    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var database : PoseDatabase
    private  lateinit var adapter : PoseListAdapter
    private lateinit var auth: FirebaseAuth

    private lateinit var posesQuery : List<Pose>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val animDrawable = viewBinding.rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        database= PoseDatabase.getDatabase(applicationContext)

        auth= Firebase.auth

        adapter = PoseListAdapter(database.poseDao().getAll(),applicationContext, this, resources,packageName)
        viewBinding.rvDances.adapter = adapter
        viewBinding.rvDances.layoutManager = LinearLayoutManager(this)

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            val db = FirebaseFirestore.getInstance()
            var user : User = User(false,false)

            val docRef=  db.collection("Users").document(currentUser.uid)
            docRef.get().addOnSuccessListener{
                    documentSnapshot ->
                val userRef=documentSnapshot.toObject(User::class.java)
                if (userRef != null) {
                    user=userRef
                }
                posesQuery = if(user.pregnant || user.injured){
                    if(user.pregnant && user.injured){
                        database.poseDao().getInjPregSafe()
                    }else if (user.pregnant && !user.injured){
                        database.poseDao().getPregSafe()
                    }else{
                        database.poseDao().getInjSafe()
                    }
                }else{
                    database.poseDao().getAll()
                }
                adapter.setItems(posesQuery)
            }
        }

    }


    override fun OnItemClick(position: Int) {
        adapter.notifyDataSetChanged()
        displayModePickerDialog(position)
           }

    private fun displayModePickerDialog(position: Int) {
        val popupDialog = Dialog(this)
        popupDialog.setCancelable(true)
        popupDialog.setContentView(R.layout.mode_picker_dialog)
        popupDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val image=popupDialog.findViewById<ImageView>(R.id.move_image)
        val name=popupDialog.findViewById<TextView>(R.id.move_name)
        val detail=popupDialog.findViewById<TextView>(R.id.move_detail)
        val moreDetail=popupDialog.findViewById<TextView>(R.id.move_more_detail)
        val learnButton:Button=popupDialog.findViewById(R.id.learn)
        val practiceButton:Button=popupDialog.findViewById(R.id.practice)
        var codeName = when(posesQuery[position].type){
            Pose.Type.YOGA -> "yoga "
            Pose.Type.DANCE -> "dance "
        }
        codeName+= posesQuery[position].name
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
        val dataObject=posesQuery[position]
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