package onlab.mlkit.tiktok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import onlab.mlkit.tiktok.data.User
import onlab.mlkit.tiktok.databinding.ActivityMainBinding
import onlab.mlkit.tiktok.databinding.ActivitySettingsBinding

class SettingsActivity() : DrawerBaseActivity() {
    lateinit var viewBinding: ActivitySettingsBinding
    lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.rootLayout)

        allocateActivityTitle("Settings")

        val pregSwitch = findViewById<Switch>(R.id.pregnantSwitch)
        val injSwitch = findViewById<Switch>(R.id.injuredSwitch)
        var btn = findViewById<Button>(R.id.setbtn)

        val db=FirebaseFirestore.getInstance()

        userId = FirebaseAuth.getInstance().currentUser!!.uid

        db.collection("Users").document(userId).get()
            .addOnSuccessListener {
                    documentSnapshot ->
                val userRef=documentSnapshot.toObject(User::class.java)
                println(userRef)
                if ( userRef != null){
                    if(userRef.pregnant)
                        pregSwitch.isChecked=true
                    if (userRef.injured)
                        injSwitch.isChecked=true
                }
            }


        btn.setOnClickListener {
            saveFireStore(pregSwitch.isChecked,injSwitch.isChecked)
        }
    }

    private fun saveFireStore(preg: Boolean, inj: Boolean) {
        val db = FirebaseFirestore.getInstance()

        val mapUpdate = mapOf(
            "pregnant" to preg,
            "injured" to inj
        )

        db.collection("Users")
            .document(userId)
            .update(mapUpdate)
            .addOnSuccessListener {
                Toast.makeText(this,"Settings changed successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Settings failed to change", Toast.LENGTH_SHORT).show()
            }
    }
}