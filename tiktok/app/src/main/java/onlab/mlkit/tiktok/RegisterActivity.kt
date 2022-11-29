package onlab.mlkit.tiktok

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import onlab.mlkit.tiktok.databinding.ActivityMainBinding
import onlab.mlkit.tiktok.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityRegisterBinding
    lateinit var regEmail : TextInputEditText
    lateinit var regPass : TextInputEditText
    lateinit var regBtn : Button
    lateinit var loginSwitch : TextView

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        regEmail=findViewById(R.id.etRegEmail)
        regPass=findViewById(R.id.etRegPass)
        regBtn=findViewById(R.id.regBtn)
        loginSwitch=findViewById(R.id.loginSwitch)

        auth = FirebaseAuth.getInstance()

        val animDrawable = viewBinding.rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        regBtn.setOnClickListener {
            createUser()
        }

        loginSwitch.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun createUser() {
        val email = regEmail.text.toString()
        val pass = regPass.text.toString()

        if(TextUtils.isEmpty(email)){
            regEmail.error = "Email cannot be empty"
            regEmail.requestFocus()
        }else if(TextUtils.isEmpty(pass)) {
            regPass.error = "Password cannot be empty"
            regPass.requestFocus()
        } else {
            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    saveFireStore()
                    startActivity(Intent(this, LoginActivity::class.java))
                }else {
                    Toast.makeText(this,"Registration Error: " + it.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveFireStore() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance().currentUser

        val user = hashMapOf(
            "injured" to false,
            "pregnant" to false
        )

        db.collection("Users")
            .document(auth!!.uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this,"Settings changed successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Settings failed to change", Toast.LENGTH_SHORT).show()
            }

    }
}